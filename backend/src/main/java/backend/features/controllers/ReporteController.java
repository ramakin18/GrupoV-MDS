package backend.features.controllers;

import backend.configs.BaseResponse;
import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.services.interfaces.domain.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST})
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para generaci?n de reportes")
public class ReporteController {

    private final IReporteService reporteService;

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoResponseDto>> getProductosMasVendidos(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        System.out.println(">>> [DEBUG] Backend recibió: MES " + mes + ", ANIO " + anio);

        List<ProductoMasVendidoResponseDto> report = reporteService.getProductosMasVendidos(mes, anio);

        return ResponseEntity.ok(report);
    }
}
