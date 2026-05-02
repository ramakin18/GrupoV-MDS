package backend.features.controllers;

import backend.configs.BaseResponse;
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
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final IClienteService clienteService;

    @PostMapping("/registrar")
    public ResponseEntity<BaseResponse<ClienteResponseDto>> registrar(@Valid @RequestBody ClienteCreateRequestDto request) {
        ClienteResponseDto response = clienteService.register(request);
        return ResponseEntity.ok(BaseResponse.ok(response, "Cliente registrado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ClienteResponseDto>> login(@Valid @RequestBody ClienteLoginRequestDto request) {
        ClienteResponseDto response = clienteService.login(request);
        return ResponseEntity.ok(BaseResponse.ok(response, "Login exitoso"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ClienteResponseDto>>> getAll() {
        List<ClienteResponseDto> clientes = clienteService.getAll();
        return ResponseEntity.ok(BaseResponse.ok(clientes, "Clientes listados correctamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ClienteResponseDto>> getById(@PathVariable Long id) {
        ClienteResponseDto cliente = clienteService.getById(id);
        return ResponseEntity.ok(BaseResponse.ok(cliente, "Cliente encontrado"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ClienteResponseDto>> update(@PathVariable Long id, @Valid @RequestBody ClienteCreateRequestDto request) {
        ClienteResponseDto cliente = clienteService.update(id, request);
        return ResponseEntity.ok(BaseResponse.ok(cliente, "Cliente actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Cliente eliminado correctamente"));
    }
}
