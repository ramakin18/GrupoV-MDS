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
}
