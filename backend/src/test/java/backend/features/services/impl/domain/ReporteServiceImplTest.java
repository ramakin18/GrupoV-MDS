package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.dtos.response.StockMinimoReporteResponseDto;
import backend.features.models.Kit;
import backend.features.models.KitProducto;
import backend.features.models.Producto;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.PedidoDetalleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock private PedidoDetalleRepository pedidoDetalleRepository;
    @Mock private IProductoRepository productoRepository;
    @Mock private IKitRepository kitRepository;

    private ReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReporteServiceImpl(pedidoDetalleRepository, productoRepository, kitRepository);
    }

    @Test
    void getProductosCercaStockMinimo_excluyeProductosPorEncimaDeLaCota() {
        Producto dentroDelMinimo = producto(1L, "Producto Minimo", 10, 10);
        Producto sinStock = producto(2L, "Producto Sin Stock", 0, 5);
        Producto dentroDelPorcentaje = producto(3L, "Producto Cerca", 12, 10);
        Producto fueraDelPorcentaje = producto(4L, "Producto Sobrante", 13, 10);

        when(productoRepository.findByBorradoFalse()).thenReturn(List.of(
            dentroDelMinimo,
            sinStock,
            dentroDelPorcentaje,
            fueraDelPorcentaje
        ));
        when(kitRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of());

        List<StockMinimoReporteResponseDto> result = service.getProductosCercaStockMinimo();

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(item -> item.codigo().equals("PROD-1")));
        assertTrue(result.stream().anyMatch(item -> item.codigo().equals("PROD-2")));
        assertTrue(result.stream().anyMatch(item -> item.codigo().equals("PROD-3")));
        assertFalse(result.stream().anyMatch(item -> item.codigo().equals("PROD-4")));
    }

    @Test
    void getProductosCercaStockMinimo_incluyeKitsActivosDentroDeLaCota() {
        Producto componente = producto(10L, "Componente", 0, 10);
        Kit kit = Kit.builder()
            .idKit(1L)
            .nombre("Kit Reponible")
            .stock(6)
            .activo(true)
            .productos(List.of(KitProducto.builder()
                .producto(componente)
                .cantidad(2)
                .build()))
            .build();

        when(productoRepository.findByBorradoFalse()).thenReturn(List.of());
        when(kitRepository.findByActivoTrueOrderByNombreAsc()).thenReturn(List.of(kit));

        List<StockMinimoReporteResponseDto> result = service.getProductosCercaStockMinimo();

        assertEquals(1, result.size());
        StockMinimoReporteResponseDto item = result.get(0);
        assertEquals("KIT-1", item.codigo());
        assertEquals("Kit Reponible", item.nombre());
        assertEquals(6, item.stockActual());
        assertEquals(5, item.stockMinimo());
    }

    @Test
    void getProductosMasVendidos_conMesYAnio_usaRangoMensual() {
        when(pedidoDetalleRepository.findProductosMasVendidos(
            LocalDateTime.of(2026, 6, 1, 0, 0),
            LocalDateTime.of(2026, 7, 1, 0, 0)
        )).thenReturn(List.of(new ProductoMasVendidoResponseDto("Producto A", 5L)));

        List<ProductoMasVendidoResponseDto> result = service.getProductosMasVendidos(6, 2026, null);

        assertEquals(1, result.size());
        assertEquals("Producto A", result.get(0).getNombreProducto());
    }

    @Test
    void getProductosMasVendidos_conDia_usaRangoDiario() {
        service.getProductosMasVendidos(6, 2026, 4);

        verify(pedidoDetalleRepository).findProductosMasVendidos(
            LocalDateTime.of(2026, 6, 4, 0, 0),
            LocalDateTime.of(2026, 6, 5, 0, 0)
        );
    }

    @Test
    void getProductosMasVendidos_sinFiltros_consultaTodo() {
        service.getProductosMasVendidos(null, null, null);

        ArgumentCaptor<LocalDateTime> desde = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> hasta = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(pedidoDetalleRepository).findProductosMasVendidos(desde.capture(), hasta.capture());
        assertNull(desde.getValue());
        assertNull(hasta.getValue());
    }

    @Test
    void getProductosMasVendidos_conDiaSinMesYAnio_shouldThrow() {
        assertThrows(ValidationException.class, () -> service.getProductosMasVendidos(null, null, 4));
    }

    private Producto producto(Long id, String nombre, Integer stockDisponible, Integer stockMinimo) {
        return Producto.builder()
            .idProducto(id)
            .nombreProducto(nombre)
            .stockDisponible(stockDisponible)
            .stockMinimo(stockMinimo)
            .borrado(false)
            .build();
    }
}
