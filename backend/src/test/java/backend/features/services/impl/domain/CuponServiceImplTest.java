package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.request.CuponCreateRequest;
import backend.features.dtos.request.PedidoItemRequest;
import backend.features.dtos.response.CuponAplicacionResponseDto;
import backend.features.dtos.response.CuponResponseDto;
import backend.features.models.Cliente;
import backend.features.models.Cupon;
import backend.features.models.CuponCliente;
import backend.features.models.Producto;
import backend.features.models.TipoDescuento;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.CuponClienteRepository;
import backend.features.repositories.CuponRepository;
import backend.features.repositories.IProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceImplTest {

    @Mock private CuponRepository cuponRepository;
    @Mock private CuponClienteRepository cuponClienteRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private IProductoRepository productoRepository;
    @Mock private CuponEmailService cuponEmailService;

    private CuponServiceImpl service;
    private Cliente cliente;
    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new CuponServiceImpl(
            cuponRepository,
            cuponClienteRepository,
            clienteRepository,
            productoRepository,
            cuponEmailService
        );

        cliente = Cliente.builder()
            .id(1L)
            .nombre("Ana")
            .apellido("Lopez")
            .email("ana@test.com")
            .contrasena("12345678")
            .pais("Argentina")
            .provincia("BA")
            .localidad("CABA")
            .calle("Calle")
            .numero("123")
            .rol("CLIENTE")
            .build();

        producto = Producto.builder()
            .idProducto(1L)
            .nombreProducto("Pintura")
            .precio(BigDecimal.valueOf(100))
            .stockDisponible(5)
            .borrado(false)
            .build();
    }

    @Test
    void create_conDescuentoPorcentualValido_shouldGenerarCodigoYEnviarMail() {
        CuponCreateRequest request = new CuponCreateRequest(
            List.of(1L),
            List.of(),
            TipoDescuento.PORCENTAJE,
            BigDecimal.valueOf(15),
            "01/06/2026",
            "30/06/2026"
        );

        when(clienteRepository.findAllById(any(Iterable.class))).thenReturn(List.of(cliente));
        when(cuponRepository.existsByCodigo(any())).thenReturn(false);
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CuponResponseDto response = service.create(request);

        assertNotNull(response.codigo());
        assertTrue(response.codigo().matches("\\d{8}"));
        assertEquals(1, response.mailsEnviados());
        verify(cuponEmailService).sendCuponEmail(eq(cliente), any(Cupon.class));
    }

    @Test
    void create_conFechaMalFormateada_shouldThrow() {
        CuponCreateRequest request = new CuponCreateRequest(
            List.of(1L),
            List.of(),
            TipoDescuento.MONTO_FIJO,
            BigDecimal.valueOf(10),
            "2026-06-01",
            "30/06/2026"
        );

        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(request));
        assertTrue(ex.getMessage().contains("dd/mm/aaaa"));
    }

    @Test
    void aplicar_conCuponValido_shouldCalcularDescuento() {
        Cupon cupon = validCoupon(TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(10));
        CuponCliente asignacion = CuponCliente.builder()
            .cupon(cupon)
            .cliente(cliente)
            .usado(false)
            .build();

        when(cuponRepository.findByCodigo("12345678")).thenReturn(Optional.of(cupon));
        when(cuponClienteRepository.findByCuponCodigoAndClienteId("12345678", 1L)).thenReturn(Optional.of(asignacion));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));

        CuponAplicacionResponseDto response = service.aplicar(
            new backend.features.dtos.request.CuponAplicacionRequest(
                1L,
                "12345678",
                List.of(new PedidoItemRequest(1L, 1))
            )
        );

        assertEquals(BigDecimal.valueOf(100).setScale(2), response.subtotal());
        assertEquals(BigDecimal.valueOf(10).setScale(2), response.descuento());
        assertEquals(BigDecimal.valueOf(90).setScale(2), response.totalConDescuento());
    }

    @Test
    void aplicar_cuponDeOtroCliente_shouldThrow() {
        Cupon cupon = validCoupon(TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(10));
        when(cuponRepository.findByCodigo("12345678")).thenReturn(Optional.of(cupon));
        when(cuponClienteRepository.findByCuponCodigoAndClienteId("12345678", 2L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () ->
            service.validateForOrder(2L, "12345678", List.of(new PedidoItemRequest(1L, 1))));
    }

    @Test
    void aplicar_cuponVencido_shouldThrow() {
        Cupon cupon = validCoupon(TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(10));
        cupon.setFechaDesde(LocalDate.now().minusDays(10));
        cupon.setFechaHasta(LocalDate.now().minusDays(1));
        CuponCliente asignacion = CuponCliente.builder().cupon(cupon).cliente(cliente).usado(false).build();

        when(cuponRepository.findByCodigo("12345678")).thenReturn(Optional.of(cupon));
        when(cuponClienteRepository.findByCuponCodigoAndClienteId("12345678", 1L)).thenReturn(Optional.of(asignacion));

        assertThrows(ValidationException.class, () ->
            service.validateForOrder(1L, "12345678", List.of(new PedidoItemRequest(1L, 1))));
    }

    @Test
    void aplicar_cuponUsado_shouldThrow() {
        Cupon cupon = validCoupon(TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(10));
        CuponCliente asignacion = CuponCliente.builder().cupon(cupon).cliente(cliente).usado(true).build();

        when(cuponRepository.findByCodigo("12345678")).thenReturn(Optional.of(cupon));
        when(cuponClienteRepository.findByCuponCodigoAndClienteId("12345678", 1L)).thenReturn(Optional.of(asignacion));

        assertThrows(ValidationException.class, () ->
            service.validateForOrder(1L, "12345678", List.of(new PedidoItemRequest(1L, 1))));
    }

    @Test
    void aplicar_descuentoIgualAlTotal_shouldThrow() {
        Cupon cupon = validCoupon(TipoDescuento.MONTO_FIJO, BigDecimal.valueOf(100));
        CuponCliente asignacion = CuponCliente.builder().cupon(cupon).cliente(cliente).usado(false).build();

        when(cuponRepository.findByCodigo("12345678")).thenReturn(Optional.of(cupon));
        when(cuponClienteRepository.findByCuponCodigoAndClienteId("12345678", 1L)).thenReturn(Optional.of(asignacion));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));

        assertThrows(ValidationException.class, () ->
            service.validateForOrder(1L, "12345678", List.of(new PedidoItemRequest(1L, 1))));
    }

    private Cupon validCoupon(TipoDescuento tipo, BigDecimal valor) {
        return Cupon.builder()
            .idCupon(1L)
            .codigo("12345678")
            .tipoDescuento(tipo)
            .valor(valor.setScale(2))
            .fechaDesde(LocalDate.now().minusDays(1))
            .fechaHasta(LocalDate.now().plusDays(1))
            .productos(List.of())
            .build();
    }
}
