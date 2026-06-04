package backend.features.controllers;

import backend.features.dtos.request.CuponAplicacionRequest;
import backend.features.dtos.request.CuponCreateRequest;
import backend.features.dtos.response.CuponAplicacionResponseDto;
import backend.features.dtos.response.CuponResponseDto;
import backend.features.services.interfaces.domain.ICuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cupones")
@AllArgsConstructor
@Tag(name = "Cupones", description = "Generacion y aplicacion de cupones de descuento")
public class CuponController {

    private final ICuponService cuponService;

    @GetMapping
    @Operation(summary = "Listar cupones", description = "Obtiene los cupones generados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de cupones",
            content = @Content(schema = @Schema(implementation = CuponResponseDto.class)))
    })
    public ResponseEntity<List<CuponResponseDto>> getAll() {
        return ResponseEntity.ok(cuponService.getAll());
    }

    @PostMapping
    @Operation(summary = "Generar cupon", description = "Genera un cupon para los clientes seleccionados")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cupon generado",
            content = @Content(schema = @Schema(implementation = CuponResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public ResponseEntity<CuponResponseDto> create(@Valid @RequestBody CuponCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.create(request));
    }

    @PostMapping("/aplicar")
    @Operation(summary = "Aplicar cupon", description = "Valida un cupon y calcula el descuento sobre el carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cupon aplicado",
            content = @Content(schema = @Schema(implementation = CuponAplicacionResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Cupon invalido")
    })
    public ResponseEntity<CuponAplicacionResponseDto> aplicar(@Valid @RequestBody CuponAplicacionRequest request) {
        return ResponseEntity.ok(cuponService.aplicar(request));
    }
}
