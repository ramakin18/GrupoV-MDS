package backend.features.controllers;

import backend.features.dtos.request.KitProductoCreateReqDto;
import backend.features.dtos.response.KitProductoResponseDto;
import backend.features.services.interfaces.domain.IKitProductoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kits")
@AllArgsConstructor
public class KitProductoController {

    private final IKitProductoService kitProductoService;

    @PostMapping
    public ResponseEntity<KitProductoResponseDto> create(
            @Valid @RequestBody KitProductoCreateReqDto request
    ) {
        return ResponseEntity.ok(kitProductoService.create(request));
    }
}