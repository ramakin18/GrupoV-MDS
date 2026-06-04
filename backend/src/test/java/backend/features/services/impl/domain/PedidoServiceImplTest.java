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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private IProductoRepository productoRepository;
    @Mock private PedidoMapper pedidoMapper;
    @Mock private ICuponService cuponService;

    private PedidoServiceImpl service;
    private Cliente cliente;
    private Producto producto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        service = new PedidoServiceImpl(pedidoRepository, clienteRepository, productoRepository, pedidoMapper, cuponService);

        cliente = Cliente.builder()
            .id(1L).nombre("Juan").apellido("Perez")
            .email("juan@test.com").contrasena("pass")
            .pais("Argentina").provincia("BSAS").localidad("CABA")
            .calle("Av Siempreviva").numero("123")
            .rol("CLIENTE").build();

        producto = Producto.builder()
            .idProducto(1L).nombreProducto("Producto Test")
            .precio(BigDecimal.valueOf(100)).stockDisponible(10).borrado(false).build();

        pedido = Pedido.builder()
            .idPedido(1L).cliente(cliente).fecha(LocalDateTime.now())
            .situacion(SituacionPedido.RESERVADO).total(BigDecimal.valueOf(200))
            .formaPago("EFECTIVO").build();
    }

    /* ================== CREATE ================== */

    @Test
    void create_conProductosConStock_shouldDescontarStock() {
        PedidoItemRequest item = new PedidoItemRequest(1L, 2);
        PedidoCreateRequest request = new PedidoCreateRequest(1L, List.of(item), null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

        PedidoResponseDTO result = service.create(request);

        assertNotNull(result);
        assertEquals(8, producto.getStockDisponible()); // 10 - 2
        verify(productoRepository).save(producto);
    }

    @Test
    void create_conClienteInexistente_shouldThrow() {
        PedidoItemRequest item = new PedidoItemRequest(1L, 1);
        PedidoCreateRequest request = new PedidoCreateRequest(99L, List.of(item), null, null);

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void create_conProductoInexistente_shouldThrow() {
        PedidoItemRequest item = new PedidoItemRequest(99L, 1);
        PedidoCreateRequest request = new PedidoCreateRequest(1L, List.of(item), null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findByIdProductoAndBorradoFalse(99L)).thenReturn(Optional.empty());

        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(request));
        assertTrue(ex.getMessage().contains("no existe o fue desactivado"));
    }

    @Test
    void create_conStockInsuficiente_shouldThrow() {
        PedidoItemRequest item = new PedidoItemRequest(1L, 99);
        PedidoCreateRequest request = new PedidoCreateRequest(1L, List.of(item), null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));

        ValidationException ex = assertThrows(ValidationException.class, () -> service.create(request));
        assertTrue(ex.getMessage().contains("No hay stock suficiente"));
    }

    @Test
    void create_debeCrearEnEstadoReservado() {
        PedidoItemRequest item = new PedidoItemRequest(1L, 2);
        PedidoCreateRequest request = new PedidoCreateRequest(1L, List.of(item), null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

        service.create(request);

        verify(pedidoRepository).save(argThat(p -> p.getSituacion() == SituacionPedido.RESERVADO));
    }

    @Test
    void create_formaPagoDebeSerEfectivo() {
        PedidoItemRequest item = new PedidoItemRequest(1L, 2);
        PedidoCreateRequest request = new PedidoCreateRequest(1L, List.of(item), null, null);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

        service.create(request);

        verify(pedidoRepository).save(argThat(p -> "EFECTIVO".equals(p.getFormaPago())));
    }

    /* ================== CANCEL ================== */

    @Test
    void cancelar_conMotivoValido_shouldCancel() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

        PedidoResponseDTO result = service.cancelar(1L, "Cliente no conforme");

        assertNotNull(result);
        verify(pedidoRepository).save(argThat(p ->
            p.getSituacion() == SituacionPedido.CANCELADO
            && "Cliente no conforme".equals(p.getMotivoCancelacion())));
    }

    @Test
    void cancelar_sinMotivo_shouldThrow() {
        assertThrows(ValidationException.class, () -> service.cancelar(1L, ""));
    }

    @Test
    void cancelar_pedidoYaEntregado_shouldThrow() {
        pedido.setSituacion(SituacionPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ValidationException.class, () -> service.cancelar(1L, "motivo"));
    }

    @Test
    void cancelar_pedidoRetirado_shouldThrow() {
        pedido.setSituacion(SituacionPedido.RETIRADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        ValidationException ex = assertThrows(ValidationException.class, () -> service.cancelar(1L, "motivo"));
        assertTrue(ex.getMessage().contains("proveedor de envios"));
    }

    @Test
    void cancelar_pedidoYaCancelado_shouldThrow() {
        pedido.setSituacion(SituacionPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ValidationException.class, () -> service.cancelar(1L, "motivo"));
    }

    /* ================== UPDATE SITUACION ================== */

    @Test
    void updateSituacion_pedidoCancelado_shouldThrow() {
        pedido.setSituacion(SituacionPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ValidationException.class, () -> service.updateSituacion(1L, SituacionPedido.PENDIENTE));
    }

    @Test
    void updateSituacion_intentarCancelar_shouldThrow() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(ValidationException.class, () -> service.updateSituacion(1L, SituacionPedido.CANCELADO));
    }

    @Test
    void updateSituacion_transicionValida_shouldUpdate() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDto(any(Pedido.class))).thenReturn(mock(PedidoResponseDTO.class));

        PedidoResponseDTO result = service.updateSituacion(1L, SituacionPedido.LISTO);

        assertNotNull(result);
        verify(pedidoRepository).save(argThat(p -> p.getSituacion() == SituacionPedido.LISTO));
    }
}
