package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.request.CarritoItemRequestDto;
import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoResponseDto;
import backend.features.models.Producto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock private IProductoRepository productoRepository;

    private CarritoServiceImpl service;
    private Producto producto;

    @BeforeEach
    void setUp() {
        service = new CarritoServiceImpl(productoRepository);

        producto = Producto.builder()
            .idProducto(1L).nombreProducto("Producto Test")
            .precio(BigDecimal.valueOf(100)).stockDisponible(10).borrado(false).build();
    }

    @Test
    void validar_conStockSuficiente_shouldReturnItems() {
        CarritoItemRequestDto item1 = new CarritoItemRequestDto();
        item1.setIdProducto(1L);
        item1.setCantidad(3);
        CarritoValidateRequestDto request = new CarritoValidateRequestDto();
        request.setItems(List.of(item1));

        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));

        CarritoResponseDto result = service.validar(request);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(BigDecimal.valueOf(300), result.getTotal());
    }

    @Test
    void validar_conStockInsuficiente_shouldThrow() {
        CarritoItemRequestDto item1 = new CarritoItemRequestDto();
        item1.setIdProducto(1L);
        item1.setCantidad(99);
        CarritoValidateRequestDto request = new CarritoValidateRequestDto();
        request.setItems(List.of(item1));

        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));

        ValidationException ex = assertThrows(ValidationException.class, () -> service.validar(request));
        assertTrue(ex.getMessage().contains("No hay stock suficiente"));
    }

    @Test
    void validar_conProductoInexistente_shouldThrow() {
        CarritoItemRequestDto item1 = new CarritoItemRequestDto();
        item1.setIdProducto(99L);
        item1.setCantidad(1);
        CarritoValidateRequestDto request = new CarritoValidateRequestDto();
        request.setItems(List.of(item1));

        when(productoRepository.findByIdProductoAndBorradoFalse(99L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> service.validar(request));
    }

    @Test
    void validar_conMultiplesItems_shouldCalcularTotalCorrectamente() {
        Producto p2 = Producto.builder()
            .idProducto(2L).nombreProducto("Producto B")
            .precio(BigDecimal.valueOf(50)).stockDisponible(10).borrado(false).build();

        CarritoItemRequestDto item1 = new CarritoItemRequestDto();
        item1.setIdProducto(1L);
        item1.setCantidad(2);
        CarritoItemRequestDto item2 = new CarritoItemRequestDto();
        item2.setIdProducto(2L);
        item2.setCantidad(3);
        CarritoValidateRequestDto request = new CarritoValidateRequestDto();
        request.setItems(List.of(item1, item2));

        when(productoRepository.findByIdProductoAndBorradoFalse(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.findByIdProductoAndBorradoFalse(2L)).thenReturn(Optional.of(p2));

        CarritoResponseDto result = service.validar(request);

        assertEquals(BigDecimal.valueOf(350), result.getTotal());
    }
}
