package backend.features.services.impl.domain;

import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.PedidoCreateRequest;
import backend.features.dtos.request.PedidoItemRequest;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.mappers.PedidoMapper;
import backend.features.models.*;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.PedidoRepository;
import backend.features.services.interfaces.domain.ICuponService;
import backend.features.services.interfaces.domain.IPedidoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final IProductoRepository productoRepository;
    private final PedidoMapper pedidoMapper;
    private final ICuponService cuponService;

    @Override
    public List<PedidoResponseDTO> getAll(String estado) {
        List<Pedido> pedidos;
        if (estado != null && !estado.isBlank() && !"TODOS".equalsIgnoreCase(estado)) {
            SituacionPedido situacion = SituacionPedido.valueOf(estado.toUpperCase());
            pedidos = pedidoRepository.findBySituacionOrderByFechaDesc(situacion);
        } else {
            pedidos = pedidoRepository.findAllByOrderByFechaDesc();
        }
        return pedidoMapper.toResponseDtoList(pedidos);
    }

    @Override
    public PedidoResponseDTO getById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        return pedidoMapper.toResponseDto(pedido);
    }

    @Override
    public List<PedidoResponseDTO> getPendingDelivery() {
        List<SituacionPedido> pendientes = List.of(SituacionPedido.RESERVADO, SituacionPedido.PENDIENTE);
        return pedidoMapper.toResponseDtoList(
            pedidoRepository.findBySituacionInOrderByFechaDesc(pendientes));
    }

    @Override
    @Transactional
    public PedidoResponseDTO create(PedidoCreateRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.clienteId()));

        List<PedidoDetalle> detalles = new ArrayList<>();
        List<Producto> productosAActualizar = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemRequest item : request.items()) {
            Producto producto = productoRepository.findByIdProductoAndBorradoFalse(item.idProducto())
                .orElseThrow(() -> new ValidationException(
                    "El producto con ID " + item.idProducto() + " no existe o fue desactivado"));

            if (item.cantidad() > producto.getStockDisponible()) {
                throw new ValidationException(
                    "No hay stock suficiente para " + producto.getNombreProducto()
                    + ". Stock disponible: " + producto.getStockDisponible());
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
            total = total.add(subtotal);

            PedidoDetalle detalle = PedidoDetalle.builder()
                .producto(producto)
                .cantidad(item.cantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .build();

            detalles.add(detalle);

            producto.setStockDisponible(producto.getStockDisponible() - item.cantidad());
            productosAActualizar.add(producto);
        }

        BigDecimal subtotal = total;
        BigDecimal descuento = BigDecimal.ZERO;
        ICuponService.CuponCalculation cuponCalculation = null;

        if (request.codigoCupon() != null && !request.codigoCupon().isBlank()) {
            cuponCalculation = cuponService.validateForOrder(
                request.clienteId(),
                request.codigoCupon(),
                request.items()
            );
            descuento = cuponCalculation.descuento();
            total = cuponCalculation.totalConDescuento();
        }

        String formaPago = request.formaPago() != null ? request.formaPago() : "EFECTIVO";

        Pedido pedido = Pedido.builder()
            .cliente(cliente)
            .fecha(LocalDateTime.now())
            .situacion(SituacionPedido.RESERVADO)
            .total(total)
            .subtotal(subtotal)
            .descuento(descuento)
            .cupon(cuponCalculation != null ? cuponCalculation.cupon() : null)
            .formaPago(formaPago)
            .paisEnvio(cliente.getPais())
            .provinciaEnvio(cliente.getProvincia())
            .localidadEnvio(cliente.getLocalidad())
            .calleEnvio(cliente.getCalle())
            .numeroEnvio(cliente.getNumero())
            .pisoEnvio(cliente.getPiso())
            .departamentoEnvio(cliente.getDepartamento())
            .detalles(detalles)
            .build();

        detalles.forEach(d -> d.setPedido(pedido));
        productosAActualizar.forEach(productoRepository::save);

        Pedido saved = pedidoRepository.save(pedido);
        if (cuponCalculation != null) {
            cuponService.markAsUsed(cuponCalculation.asignacion(), saved);
        }
        return pedidoMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public PedidoResponseDTO updateSituacion(Long id, SituacionPedido nuevaSituacion) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        if (pedido.getSituacion() == SituacionPedido.CANCELADO) {
            throw new ValidationException("No se puede actualizar un pedido cancelado");
        }

        if (nuevaSituacion == SituacionPedido.CANCELADO) {
            throw new ValidationException("Use el endpoint de cancelacion para cancelar un pedido");
        }

        pedido.setSituacion(nuevaSituacion);
        pedido.setFechaActualizacion(LocalDateTime.now());
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelar(Long id, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new ValidationException("Debe ingresar un motivo de cancelacion");
        }

        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        if (pedido.getSituacion() == SituacionPedido.ENTREGADO) {
            throw new ValidationException("No se puede cancelar un pedido ya entregado");
        }

        if (pedido.getSituacion() == SituacionPedido.RETIRADO) {
            throw new ValidationException("No se puede cancelar un pedido que ya fue notificado al proveedor de envios");
        }

        if (pedido.getSituacion() == SituacionPedido.CANCELADO) {
            throw new ValidationException("El pedido ya se encuentra cancelado");
        }

        pedido.setSituacion(SituacionPedido.CANCELADO);
        pedido.setMotivoCancelacion(motivo);
        pedido.setFechaActualizacion(LocalDateTime.now());
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDto(saved);
    }
}
