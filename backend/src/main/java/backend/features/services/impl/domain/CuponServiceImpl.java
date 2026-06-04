package backend.features.services.impl.domain;

import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.CuponAplicacionRequest;
import backend.features.dtos.request.CuponCreateRequest;
import backend.features.dtos.request.PedidoItemRequest;
import backend.features.dtos.response.CuponAplicacionResponseDto;
import backend.features.dtos.response.CuponClienteResponseDto;
import backend.features.dtos.response.CuponProductoResponseDto;
import backend.features.dtos.response.CuponResponseDto;
import backend.features.models.Cliente;
import backend.features.models.Cupon;
import backend.features.models.CuponCliente;
import backend.features.models.Pedido;
import backend.features.models.Producto;
import backend.features.models.TipoDescuento;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.CuponClienteRepository;
import backend.features.repositories.CuponRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.services.interfaces.domain.ICuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CuponServiceImpl implements ICuponService {

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_CODE_ATTEMPTS = 100;

    private final CuponRepository cuponRepository;
    private final CuponClienteRepository cuponClienteRepository;
    private final ClienteRepository clienteRepository;
    private final IProductoRepository productoRepository;
    private final CuponEmailService cuponEmailService;
    private final SecureRandom random = new SecureRandom();

    @Override
    public List<CuponResponseDto> getAll() {
        return cuponRepository.findAllByOrderByFechaCreacionDesc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public CuponResponseDto create(CuponCreateRequest request) {
        validateDiscountValue(request.tipoDescuento(), request.valor());
        LocalDate fechaDesde = parseDate(request.fechaDesde());
        LocalDate fechaHasta = parseDate(request.fechaHasta());

        if (fechaHasta.isBefore(fechaDesde)) {
            throw new ValidationException("La fecha hasta no puede ser anterior a la fecha desde");
        }

        List<Cliente> clientes = findClientes(request.clienteIds());
        List<Producto> productos = findProductos(request.productoIds());

        Cupon cupon = Cupon.builder()
            .codigo(generateUniqueCode())
            .tipoDescuento(request.tipoDescuento())
            .valor(normalizeMoney(request.valor()))
            .fechaDesde(fechaDesde)
            .fechaHasta(fechaHasta)
            .productos(productos)
            .build();

        List<CuponCliente> destinatarios = clientes.stream()
            .map(cliente -> CuponCliente.builder()
                .cupon(cupon)
                .cliente(cliente)
                .usado(false)
                .build())
            .toList();
        cupon.setDestinatarios(destinatarios);

        Cupon saved = cuponRepository.save(cupon);
        saved.getDestinatarios().forEach(destinatario ->
            cuponEmailService.sendCuponEmail(destinatario.getCliente(), saved));

        return toResponse(saved);
    }

    @Override
    public CuponAplicacionResponseDto aplicar(CuponAplicacionRequest request) {
        CuponCalculation calculation = validateForOrder(request.clienteId(), request.codigo(), request.items());
        return toAplicacionResponse(calculation);
    }

    @Override
    public CuponCalculation validateForOrder(Long clienteId, String codigo, List<PedidoItemRequest> items) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidationException("Debe ingresar el codigo del cupon");
        }

        Cupon cupon = cuponRepository.findByCodigo(codigo.trim())
            .orElseThrow(() -> new ValidationException("El codigo de cupon es invalido"));

        CuponCliente asignacion = cuponClienteRepository.findByCuponCodigoAndClienteId(cupon.getCodigo(), clienteId)
            .orElseThrow(() -> new ValidationException("El cupon no pertenece al cliente"));

        if (asignacion.isUsado()) {
            throw new ValidationException("El cupon ya fue usado");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(cupon.getFechaDesde()) || today.isAfter(cupon.getFechaHasta())) {
            throw new ValidationException("El cupon esta vencido o fuera de vigencia");
        }

        BigDecimal subtotal = calculateSubtotal(items);
        validateProductScope(cupon, items);

        BigDecimal descuento = calculateDiscount(cupon, subtotal);
        if (subtotal.compareTo(descuento) <= 0) {
            throw new ValidationException("El monto total del pedido debe ser mayor al descuento del cupon");
        }

        return new CuponCalculation(
            cupon,
            asignacion,
            subtotal,
            descuento,
            subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    @Transactional
    public void markAsUsed(CuponCliente asignacion, Pedido pedido) {
        asignacion.setUsado(true);
        asignacion.setFechaUso(LocalDateTime.now());
        asignacion.setPedido(pedido);
        cuponClienteRepository.save(asignacion);
    }

    private List<Cliente> findClientes(List<Long> clienteIds) {
        Set<Long> uniqueIds = new LinkedHashSet<>(clienteIds);
        List<Cliente> clientes = clienteRepository.findAllById(uniqueIds);

        if (clientes.size() != uniqueIds.size()) {
            throw new ResourceNotFoundException("Uno o mas clientes seleccionados no existen");
        }

        return clientes;
    }

    private List<Producto> findProductos(List<Long> productoIds) {
        if (productoIds == null || productoIds.isEmpty()) {
            return List.of();
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(productoIds);
        List<Producto> productos = productoRepository.findAllById(uniqueIds);

        if (productos.size() != uniqueIds.size()) {
            throw new ResourceNotFoundException("Uno o mas productos seleccionados no existen");
        }

        return productos;
    }

    private String generateUniqueCode() {
        for (int i = 0; i < MAX_CODE_ATTEMPTS; i++) {
            String codigo = String.valueOf(10_000_000 + random.nextInt(90_000_000));
            if (!cuponRepository.existsByCodigo(codigo)) {
                return codigo;
            }
        }
        throw new ValidationException("No se pudo generar un codigo de cupon unico");
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Las fechas deben tener formato dd/mm/aaaa");
        }
    }

    private void validateDiscountValue(TipoDescuento tipo, BigDecimal valor) {
        if (tipo == null) {
            throw new ValidationException("Debe seleccionar el tipo de descuento");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El descuento debe ser mayor a cero");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new ValidationException("El descuento admite hasta dos decimales");
        }
        if (tipo == TipoDescuento.PORCENTAJE && valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException("El porcentaje no puede superar el 100%");
        }
    }

    private BigDecimal calculateSubtotal(List<PedidoItemRequest> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (PedidoItemRequest item : items) {
            Producto producto = productoRepository.findByIdProductoAndBorradoFalse(item.idProducto())
                .orElseThrow(() -> new ValidationException(
                    "El producto con ID " + item.idProducto() + " no existe o fue desactivado"));

            if (item.cantidad() > producto.getStockDisponible()) {
                throw new ValidationException("No hay stock suficiente para " + producto.getNombreProducto());
            }

            subtotal = subtotal.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad())));
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateProductScope(Cupon cupon, List<PedidoItemRequest> items) {
        if (cupon.getProductos() == null || cupon.getProductos().isEmpty()) {
            return;
        }

        Set<Long> productosAbarcados = cupon.getProductos().stream()
            .map(Producto::getIdProducto)
            .collect(java.util.stream.Collectors.toSet());

        boolean hasCoveredProduct = items.stream()
            .anyMatch(item -> productosAbarcados.contains(item.idProducto()));

        if (!hasCoveredProduct) {
            throw new ValidationException("El cupon no aplica a los productos del pedido");
        }
    }

    private BigDecimal calculateDiscount(Cupon cupon, BigDecimal subtotal) {
        if (cupon.getTipoDescuento() == TipoDescuento.PORCENTAJE) {
            return subtotal
                .multiply(cupon.getValor())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return normalizeMoney(cupon.getValor());
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private CuponResponseDto toResponse(Cupon cupon) {
        List<CuponClienteResponseDto> clientes = cupon.getDestinatarios() == null
            ? List.of()
            : cupon.getDestinatarios().stream()
                .map(destinatario -> new CuponClienteResponseDto(
                    destinatario.getCliente().getId(),
                    destinatario.getCliente().getNombre(),
                    destinatario.getCliente().getApellido(),
                    destinatario.getCliente().getEmail(),
                    destinatario.isUsado()
                ))
                .toList();

        List<CuponProductoResponseDto> productos = cupon.getProductos() == null
            ? List.of()
            : cupon.getProductos().stream()
                .map(producto -> new CuponProductoResponseDto(
                    producto.getIdProducto(),
                    producto.getNombreProducto()
                ))
                .toList();

        return new CuponResponseDto(
            cupon.getIdCupon(),
            cupon.getCodigo(),
            cupon.getTipoDescuento(),
            cupon.getValor(),
            cupon.getFechaDesde().format(OUTPUT_DATE_FORMAT),
            cupon.getFechaHasta().format(OUTPUT_DATE_FORMAT),
            clientes,
            productos,
            clientes.size()
        );
    }

    private CuponAplicacionResponseDto toAplicacionResponse(CuponCalculation calculation) {
        Cupon cupon = calculation.cupon();
        return new CuponAplicacionResponseDto(
            cupon.getCodigo(),
            cupon.getTipoDescuento(),
            cupon.getValor(),
            calculation.subtotal(),
            calculation.descuento(),
            calculation.totalConDescuento(),
            cupon.getFechaDesde().format(OUTPUT_DATE_FORMAT),
            cupon.getFechaHasta().format(OUTPUT_DATE_FORMAT)
        );
    }
}
