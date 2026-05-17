package backend.features.controllers;

import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoResponseDto;
import backend.features.services.interfaces.domain.ICarritoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@AllArgsConstructor
public class CarritoController {

    private final ICarritoService carritoService;

    @PostMapping("/validar")
    public ResponseEntity<CarritoResponseDto> validar(
            @Valid @RequestBody CarritoValidateRequestDto request
    ) {
        return ResponseEntity.ok(carritoService.validar(request));
    }
}
