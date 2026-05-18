package backend.features.controllers;

import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;
import backend.features.services.interfaces.domain.IKitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kits")
@AllArgsConstructor
@Tag(name = "Kits", description = "Operaciones CRUD para kits de productos")
public class KitController {

    private final IKitService kitService;

    @GetMapping
    @Operation(summary = "Listar kits", description = "Obtiene todos los kits, opcionalmente solo los activos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de kits",
            content = @Content(schema = @Schema(implementation = KitResponseDto.class)))
    })
    public ResponseEntity<List<KitResponseDto>> getAll(
            @Parameter(description = "Filtrar solo kits activos") @RequestParam(required = false) Boolean activos) {
        if (Boolean.TRUE.equals(activos)) {
            return ResponseEntity.ok(kitService.getActivos());
        }
        return ResponseEntity.ok(kitService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener kit por ID", description = "Obtiene un kit específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kit encontrado",
            content = @Content(schema = @Schema(implementation = KitResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Kit no encontrado")
    })
    public ResponseEntity<KitResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kitService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear kit", description = "Crea un nuevo kit con productos asociados")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Kit creado exitosamente",
            content = @Content(schema = @Schema(implementation = KitResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o producto no encontrado")
    })
    public ResponseEntity<KitResponseDto> create(@Valid @RequestBody KitCreateRequest request) {
        KitResponseDto response = kitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar kit", description = "Actualiza un kit existente con sus productos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kit actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = KitResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Kit no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<KitResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody KitCreateRequest request) {
        return ResponseEntity.ok(kitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar kit", description = "Elimina un kit del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kit eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Kit no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kitService.delete(id);
        return ResponseEntity.ok().build();
    }
}
