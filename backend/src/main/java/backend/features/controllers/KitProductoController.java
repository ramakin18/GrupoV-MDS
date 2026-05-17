package backend.features.controllers;

import backend.features.dtos.request.KitProductoCreateReqDto;
import backend.features.dtos.response.KitProductoResponseDto;
import backend.features.services.interfaces.domain.IKitProductoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
@AllArgsConstructor
public class KitProductoController {

    private final IKitProductoService kitProductoService;

    @GetMapping
    public ResponseEntity<List<KitProductoResponseDto>> getAll() {
        return ResponseEntity.ok(kitProductoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitProductoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kitProductoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<KitProductoResponseDto> create(
            @Valid @RequestBody KitProductoCreateReqDto request
    ) {
        return ResponseEntity.ok(kitProductoService.create(request));
    }
}