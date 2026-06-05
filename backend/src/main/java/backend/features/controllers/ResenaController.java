package backend.features.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;
import backend.features.services.interfaces.domain.IResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/resenas")
@AllArgsConstructor
@Tag(name = "Reseñas", description = "Operaciones CRUD para las valoraciones de productos")
public class ResenaController {

    private final IResenaService resenaService;

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Ver reseñas de un producto")
    public ResponseEntity<List<ResenaResponseDto>> getByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.getByProductoId(productoId));
    }

    @PostMapping
    @Operation(summary = "Crear nueva reseña")
    public ResponseEntity<ResenaResponseDto> create(@Valid @RequestBody ResenaCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.create(request));
    }

    @PutMapping("/{id}/cliente/{usuarioId}")
    @Operation(summary = "Editar reseña propia")
    public ResponseEntity<ResenaResponseDto> update(
            @PathVariable Long id, 
            @PathVariable Long usuarioId, 
            @Valid @RequestBody ResenaCreateRequestDto request) {
        return ResponseEntity.ok(resenaService.update(id, request, usuarioId));
    }

    @DeleteMapping("/{id}/cliente/{usuarioId}")
    @Operation(summary = "Eliminar reseña propia")
    public ResponseEntity<Void> deleteByCliente(@PathVariable Long id, @PathVariable Long usuarioId) {
        resenaService.deleteByCliente(id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/admin")
    @Operation(summary = "Eliminar reseña como Administrador")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable Long id) {
        resenaService.deleteByAdmin(id);
        return ResponseEntity.ok().build();
    }
}