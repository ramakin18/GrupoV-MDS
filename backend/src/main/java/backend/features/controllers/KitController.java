package backend.features.controllers;

import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;
import backend.features.services.interfaces.domain.IKitService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
@AllArgsConstructor
public class KitController {

    private final IKitService kitService;

    @GetMapping
    public ResponseEntity<List<KitResponseDto>> getAll(
            @RequestParam(required = false) Boolean activos) {
        if (Boolean.TRUE.equals(activos)) {
            return ResponseEntity.ok(kitService.getActivos());
        }
        return ResponseEntity.ok(kitService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kitService.getById(id));
    }

    @PostMapping
    public ResponseEntity<KitResponseDto> create(@Valid @RequestBody KitCreateRequest request) {
        KitResponseDto response = kitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody KitCreateRequest request) {
        return ResponseEntity.ok(kitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kitService.delete(id);
        return ResponseEntity.ok().build();
    }
}
