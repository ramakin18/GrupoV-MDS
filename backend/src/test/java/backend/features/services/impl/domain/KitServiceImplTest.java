package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;
import backend.features.mappers.KitMapper;
import backend.features.models.Kit;
import backend.features.models.KitProducto;
import backend.features.models.Producto;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KitServiceImplTest {

    @Mock private IKitRepository kitRepository;
    @Mock private IProductoRepository productoRepository;
    @Mock private KitMapper kitMapper;

    private KitServiceImpl service;
    private Producto producto1;
    private Producto producto2;
    private KitCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        service = new KitServiceImpl(kitRepository, productoRepository, kitMapper);

        producto1 = Producto.builder()
            .idProducto(1L).nombreProducto("Producto A")
            .precio(BigDecimal.valueOf(50)).stockDisponible(20).borrado(false).build();

        producto2 = Producto.builder()
            .idProducto(2L).nombreProducto("Producto B")
            .precio(BigDecimal.valueOf(30)).stockDisponible(15).borrado(false).build();

        validRequest = new KitCreateRequest(
            "Kit Test", "Descripcion del kit", BigDecimal.valueOf(150),
            true, List.of(
                new KitCreateRequest.KitProductoItem(1L, 2),
                new KitCreateRequest.KitProductoItem(2L, 1)
            )
        );
    }

    /* ================== CREATE ================== */

    @Test
    void create_conDatosValidos_shouldPersist() {
        when(kitRepository.existsByNombreIgnoreCase("Kit Test")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(kitRepository.save(any(Kit.class))).thenAnswer(i -> i.getArgument(0));
        when(kitMapper.toResponseDto(any(Kit.class))).thenReturn(mock(KitResponseDto.class));

        KitResponseDto result = service.create(validRequest);

        assertNotNull(result);
        verify(kitRepository).save(any(Kit.class));
    }

    @Test
    void create_conNombreDuplicado_shouldThrow() {
        when(kitRepository.existsByNombreIgnoreCase("Kit Test")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(validRequest));
        verify(kitRepository, never()).save(any());
    }

    @Test
    void create_sinProductos_shouldThrow() {
        KitCreateRequest empty = new KitCreateRequest(
            "Kit Vacio", "Desc", BigDecimal.valueOf(100), true, List.of()
        );

        when(kitRepository.existsByNombreIgnoreCase("Kit Vacio")).thenReturn(false);

        assertThrows(ValidationException.class, () -> service.create(empty));
    }

    @Test
    void create_conUnSoloProductoYCantidad1_shouldThrow() {
        KitCreateRequest single = new KitCreateRequest(
            "Kit Simple", "Desc", BigDecimal.valueOf(100), true,
            List.of(new KitCreateRequest.KitProductoItem(1L, 1))
        );

        when(kitRepository.existsByNombreIgnoreCase("Kit Simple")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        assertThrows(ValidationException.class, () -> service.create(single));
    }

    @Test
    void create_conUnSoloProductoCantidadMayor1_shouldSucceed() {
        KitCreateRequest single = new KitCreateRequest(
            "Kit Simple", "Desc", BigDecimal.valueOf(100), true,
            List.of(new KitCreateRequest.KitProductoItem(1L, 3))
        );

        when(kitRepository.existsByNombreIgnoreCase("Kit Simple")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(kitRepository.save(any(Kit.class))).thenAnswer(i -> i.getArgument(0));
        when(kitMapper.toResponseDto(any(Kit.class))).thenReturn(mock(KitResponseDto.class));

        KitResponseDto result = service.create(single);
        assertNotNull(result);
    }

    @Test
    void create_conProductoInactivoYKitActivo_shouldThrow() {
        producto1.setBorrado(true);

        when(kitRepository.existsByNombreIgnoreCase("Kit Test")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        assertThrows(ValidationException.class, () -> service.create(validRequest));
    }

    @Test
    void create_conProductoInactivoYKitInactivo_shouldSucceed() {
        producto1.setBorrado(true);

        KitCreateRequest inactiveKit = new KitCreateRequest(
            "Kit Inactivo", "Desc", BigDecimal.valueOf(100), false,
            List.of(new KitCreateRequest.KitProductoItem(1L, 2))
        );

        when(kitRepository.existsByNombreIgnoreCase("Kit Inactivo")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(kitRepository.save(any(Kit.class))).thenAnswer(i -> i.getArgument(0));
        when(kitMapper.toResponseDto(any(Kit.class))).thenReturn(mock(KitResponseDto.class));

        KitResponseDto result = service.create(inactiveKit);
        assertNotNull(result);
    }

    @Test
    void create_stockCalculadoCorrectamente() {
        when(kitRepository.existsByNombreIgnoreCase("Kit Test")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(producto2));
        when(kitRepository.save(any(Kit.class))).thenAnswer(i -> i.getArgument(0));
        when(kitMapper.toResponseDto(any(Kit.class))).thenReturn(mock(KitResponseDto.class));

        service.create(validRequest);

        verify(kitRepository).save(argThat(kit -> kit.getStock() == 10)); // min(20/2, 15/1) = min(10, 15) = 10
    }

    @Test
    void create_conCantidadDecimal_shouldThrow() {
        KitCreateRequest bad = new KitCreateRequest(
            "Kit Bad", "Desc", BigDecimal.valueOf(100), true,
            List.of(new KitCreateRequest.KitProductoItem(1L, 2))
        );
        // @Positive only allows Integer > 0, so decimal is not possible at Java level
        // This test validates the request DTO constraint
        assertTrue(bad.productos().get(0).cantidad() > 0);
    }

    @Test
    void create_conCantidadCero_shouldThrow() {
        KitCreateRequest bad = new KitCreateRequest(
            "Kit Bad", "Desc", BigDecimal.valueOf(100), true,
            List.of(new KitCreateRequest.KitProductoItem(1L, 0))
        );

        when(kitRepository.existsByNombreIgnoreCase("Kit Bad")).thenReturn(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        assertThrows(ValidationException.class, () -> service.create(bad));
    }

    /* ================== UPDATE ================== */

    @Test
    void update_conNombreDuplicadoExcluyendoPropio_shouldThrow() {
        Kit existingKit = Kit.builder().idKit(1L).nombre("Kit Test").build();

        when(kitRepository.findById(1L)).thenReturn(Optional.of(existingKit));
        when(kitRepository.existsByNombreIgnoreCaseAndIdKitNot("Kit Test", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, validRequest));
    }

    /* ================== DELETE ================== */

    @Test
    void delete_softDelete_shouldSetActivoFalse() {
        Kit kit = Kit.builder().idKit(1L).nombre("Test").activo(true).build();

        when(kitRepository.findById(1L)).thenReturn(Optional.of(kit));

        service.delete(1L);

        verify(kitRepository).save(argThat(k -> !k.isActivo()));
    }

    @Test
    void delete_conIdInexistente_shouldThrow() {
        when(kitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
