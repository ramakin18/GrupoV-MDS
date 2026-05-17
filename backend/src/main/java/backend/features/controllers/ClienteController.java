package backend.features.controllers;

import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.services.interfaces.domain.IClienteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@AllArgsConstructor
public class ClienteController {

    private final IClienteService clienteService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody ClienteCreateRequestDto request) {
        try {
            // Intentamos registrar normalmente
            ClienteResponseDto response = clienteService.register(request);
            return ResponseEntity.ok(response);
        } catch (backend.exceptions.DuplicateResourceException e) {
            // Si el service tira la excepción del email, devolvemos un 400
            // con el mensaje "El email ya se encuentra registrado"
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ClienteResponseDto> login(@Valid @RequestBody ClienteLoginRequestDto request) {
        ClienteResponseDto response = clienteService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> getAll() {
        List<ClienteResponseDto> clientes = clienteService.getAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        ClienteResponseDto cliente = clienteService.getById(id);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> update(@PathVariable Long id, @Valid @RequestBody ClienteCreateRequestDto request) {
        ClienteResponseDto cliente = clienteService.update(id, request);
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
