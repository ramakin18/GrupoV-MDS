package backend.features.controllers;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.dtos.response.StockMinimoReporteResponseDto;
import backend.features.services.interfaces.domain.IReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReporteService reporteService;

    @Test
    void getProductosMasVendidos_shouldPassFilters() throws Exception {
        when(reporteService.getProductosMasVendidos(6, 2026, 4))
            .thenReturn(List.of(new ProductoMasVendidoResponseDto("Producto A", 12L)));

        mockMvc.perform(get("/api/reportes/productos-mas-vendidos?mes=6&anio=2026&dia=4"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombreProducto").value("Producto A"))
            .andExpect(jsonPath("$[0].cantidadVendida").value(12));

        verify(reporteService).getProductosMasVendidos(6, 2026, 4);
    }

    @Test
    void getStockMinimo_shouldReturnItems() throws Exception {
        when(reporteService.getProductosCercaStockMinimo())
            .thenReturn(List.of(new StockMinimoReporteResponseDto("PROD-1", "Producto A", 8, 10)));

        mockMvc.perform(get("/api/reportes/stock-minimo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codigo").value("PROD-1"))
            .andExpect(jsonPath("$[0].nombre").value("Producto A"))
            .andExpect(jsonPath("$[0].stockActual").value(8))
            .andExpect(jsonPath("$[0].stockMinimo").value(10));
    }
}
