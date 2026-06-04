package backend.features.controllers;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.dtos.response.StockMinimoReporteResponseDto;
import backend.features.services.interfaces.domain.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST})
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para generacion de reportes")
public class ReporteController {

    private final IReporteService reporteService;

    @GetMapping("/productos-mas-vendidos")
    @Operation(summary = "Reporte de productos mas vendidos")
    public ResponseEntity<List<ProductoMasVendidoResponseDto>> getProductosMasVendidos(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer dia) {
        return ResponseEntity.ok(reporteService.getProductosMasVendidos(mes, anio, dia));
    }

    @GetMapping("/stock-minimo")
    @Operation(summary = "Reporte de stock minimo", description = "Lista productos y kits cercanos o por debajo del stock minimo definido")
    public ResponseEntity<List<StockMinimoReporteResponseDto>> getProductosCercaStockMinimo() {
        return ResponseEntity.ok(reporteService.getProductosCercaStockMinimo());
    }
}
