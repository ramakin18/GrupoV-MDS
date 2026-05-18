package backend.features.controllers;

import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.request.DomicilioUpdateRequest;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.services.interfaces.domain.IClienteService;
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

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@AllArgsConstructor
@Tag(name = "Clientes", description = "Operaciones CRUD para clientes")
public class ClienteController {

    private final IClienteService clienteService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente registrado exitosamente",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    })
    public ResponseEntity<?> registrar(@Valid @RequestBody ClienteCreateRequestDto request) {
        try {
            ClienteResponseDto response = clienteService.register(request);
            return ResponseEntity.ok(response);
        } catch (backend.exceptions.DuplicateResourceException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un cliente con email y contraseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<ClienteResponseDto> login(@Valid @RequestBody ClienteLoginRequestDto request) {
        ClienteResponseDto response = clienteService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes registrados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de clientes",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class)))
    })
    public ResponseEntity<List<ClienteResponseDto>> getAll() {
        List<ClienteResponseDto> clientes = clienteService.getAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        ClienteResponseDto cliente = clienteService.getById(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ClienteResponseDto> update(@PathVariable Long id, @Valid @RequestBody ClienteCreateRequestDto request) {
        ClienteResponseDto cliente = clienteService.update(id, request);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}/domicilio")
    @Operation(summary = "Actualizar domicilio", description = "Actualiza solo el domicilio de un cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Domicilio actualizado",
            content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de domicilio inválidos")
    })
    public ResponseEntity<ClienteResponseDto> updateDomicilio(
            @PathVariable Long id,
            @Valid @RequestBody DomicilioUpdateRequest request) {
        return ResponseEntity.ok(clienteService.updateDomicilio(id, request.getDomicilio()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
