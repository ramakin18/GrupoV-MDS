package backend.features.controllers;

import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoResponseDto;
import backend.features.services.interfaces.domain.ICarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@AllArgsConstructor
@Tag(name = "Carrito", description = "Operaciones de validación del carrito de compras")
public class CarritoController {

    private final ICarritoService carritoService;

    @PostMapping("/validar")
    @Operation(summary = "Validar carrito", description = "Valida stock y precios de los items en el carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito validado exitosamente",
            content = @Content(schema = @Schema(implementation = CarritoResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente")
    })
    public ResponseEntity<CarritoResponseDto> validar(
            @Valid @RequestBody CarritoValidateRequestDto request
    ) {
        return ResponseEntity.ok(carritoService.validar(request));
    }
}
