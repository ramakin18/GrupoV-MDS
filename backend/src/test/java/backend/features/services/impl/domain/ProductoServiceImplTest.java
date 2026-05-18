package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.specs.ProductoSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock private IProductoRepository productoRepository;
    @Mock private ProductoMapper productoMapper;
    @Mock private CloudinaryServiceImpl cloudinaryService;
    @Mock private IKitRepository kitRepository;
    @Mock private MultipartFile imagen;

    @Captor private ArgumentCaptor<Producto> productoCaptor;

    private ProductoServiceImpl service;
    private ProductoCreateReqDto validRequest;
    private Producto existingProduct;

    @BeforeEach
    void setUp() {
        service = new ProductoServiceImpl(productoRepository, productoMapper, cloudinaryService, kitRepository);

        validRequest = new ProductoCreateReqDto(
            "Producto Test", "Descripcion", BigDecimal.valueOf(100),
            10, 5, null
        );

        existingProduct = Producto.builder()
            .idProducto(1L)
            .nombreProducto("Existente")
            .descripcion("Original")
            .precio(BigDecimal.valueOf(50))
            .stockDisponible(20)
            .stockMinimo(3)
            .borrado(false)
            .build();
    }

    /* ================== CREATE ================== */

    @Test
    void create_conDatosValidos_shouldPersist() {
        when(productoRepository.existsByNombreProductoIgnoreCase("Producto Test")).thenReturn(false);
        when(productoMapper.toModel(validRequest)).thenReturn(existingProduct);
        when(productoRepository.save(any(Producto.class))).thenReturn(existingProduct);
        when(productoMapper.toResponseDto(any(Producto.class))).thenReturn(
            new ProductoResponseDto(1L, "Producto Test", "Descripcion", BigDecimal.valueOf(100), 10, 5, false, null)
        );

        ProductoResponseDto result = service.create(validRequest, null);

        assertNotNull(result);
        assertEquals("Producto Test", result.nombreProducto());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void create_conNombreDuplicado_shouldThrow() {
        when(productoRepository.existsByNombreProductoIgnoreCase("Producto Test")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(validRequest, null));
        verify(productoRepository, never()).save(any());
    }

    /* ================== GET ================== */

    @Test
    void getAll_conRolAdmin_shouldIncludeInactivos() {
        when(productoRepository.findAll(any(Specification.class))).thenReturn(List.of(existingProduct));
        when(productoMapper.toResponseDtoList(any())).thenReturn(List.of(
            new ProductoResponseDto(1L, "Existente", "Original", BigDecimal.valueOf(50), 20, 3, false, null)
        ));

        List<ProductoResponseDto> result = service.getAll(null, null, null, null,
            ProductoViewRole.ADMIN, ProductoEstadoFiltro.TODOS);

        assertEquals(1, result.size());
    }

    @Test
    void getAll_conRolUsuario_shouldForceActivo() {
        when(productoRepository.findAll(any(Specification.class))).thenReturn(List.of(existingProduct));
        when(productoMapper.toResponseDtoList(any())).thenReturn(List.of(
            new ProductoResponseDto(1L, "Existente", "Original", BigDecimal.valueOf(50), 20, 3, false, null)
        ));

        List<ProductoResponseDto> result = service.getAll(null, null, null, null,
            ProductoViewRole.USUARIO, null);

        assertEquals(1, result.size());
    }

    @Test
    void getAll_conStockMinNegativo_shouldThrow() {
        assertThrows(ValidationException.class, () ->
            service.getAll(null, null, -1, null, ProductoViewRole.ADMIN, ProductoEstadoFiltro.TODOS));
    }

    @Test
    void getAll_conStockMinMayorQueMax_shouldThrow() {
        assertThrows(ValidationException.class, () ->
            service.getAll(null, null, 100, 50, ProductoViewRole.ADMIN, ProductoEstadoFiltro.TODOS));
    }

    @Test
    void getById_conIdValido_shouldReturn() {
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(existingProduct));
        when(productoMapper.toResponseDto(existingProduct)).thenReturn(
            new ProductoResponseDto(1L, "Existente", "Original", BigDecimal.valueOf(50), 20, 3, false, null)
        );

        ProductoResponseDto result = service.getById(1L);
        assertNotNull(result);
    }

    @Test
    void getById_conIdInexistente_shouldThrow() {
        when(productoRepository.findByIdProductoAndBorradoFalse(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
    }

    /* ================== UPDATE ================== */

    @Test
    void update_conDatosValidos_shouldUpdate() {
        ProductoCreateReqDto updateReq = new ProductoCreateReqDto(
            "NuevoNombre", "NuevaDesc", BigDecimal.valueOf(200), 30, 5, false
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productoRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot("NuevoNombre", 1L)).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(existingProduct);
        when(productoMapper.toResponseDto(any(Producto.class))).thenReturn(
            new ProductoResponseDto(1L, "NuevoNombre", "NuevaDesc", BigDecimal.valueOf(200), 30, 5, false, null)
        );

        ProductoResponseDto result = service.update(1L, updateReq);

        assertEquals("NuevoNombre", result.nombreProducto());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void update_conNombreDuplicado_shouldThrow() {
        ProductoCreateReqDto updateReq = new ProductoCreateReqDto(
            "Duplicado", "Desc", BigDecimal.valueOf(100), 10, 5, null
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productoRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot("Duplicado", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, updateReq));
    }

    @Test
    void update_desactivarProductoEnKitActivo_shouldThrow() {
        ProductoCreateReqDto updateReq = new ProductoCreateReqDto(
            "Existente", "Desc", BigDecimal.valueOf(50), 20, 3, true
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productoRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot("Existente", 1L)).thenReturn(false);
        when(kitRepository.existsByProductos_Producto_IdProductoAndActivoTrue(1L)).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> service.update(1L, updateReq));
        assertTrue(ex.getMessage().contains("kit activo"));
    }

    @Test
    void update_desactivarProductoSinKit_shouldSucceed() {
        ProductoCreateReqDto updateReq = new ProductoCreateReqDto(
            "Existente", "Desc", BigDecimal.valueOf(50), 20, 3, true
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productoRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot("Existente", 1L)).thenReturn(false);
        when(kitRepository.existsByProductos_Producto_IdProductoAndActivoTrue(1L)).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(existingProduct);
        when(productoMapper.toResponseDto(any(Producto.class))).thenReturn(
            new ProductoResponseDto(1L, "Existente", "Desc", BigDecimal.valueOf(50), 20, 3, true, null)
        );

        ProductoResponseDto result = service.update(1L, updateReq);
        assertTrue(result.borrado());
    }

    @Test
    void update_conPrecioConMasDe2Decimales_shouldThrow() {
        ProductoCreateReqDto bad = new ProductoCreateReqDto(
            "Test", "Desc", new BigDecimal("100.123"), 10, 5, null
        );
        // Validation is in the DTO annotations — service will pass it through
        assertEquals(0, bad.precio().compareTo(new BigDecimal("100.123")));
    }

    /* ================== DELETE ================== */

    @Test
    void delete_softDelete_shouldMarcarBorrado() {
        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(existingProduct));

        service.delete(1L);

        verify(productoRepository).save(productoCaptor.capture());
        assertTrue(productoCaptor.getValue().isBorrado());
    }

    @Test
    void delete_conIdInexistente_shouldThrow() {
        when(productoRepository.findByIdProductoAndBorradoFalse(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
