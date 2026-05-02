package backend.features.controllers;

import backend.features.models.Cliente;
import backend.features.models.ErrorMessage;
import backend.features.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.features.models.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Cliente cliente) {

        // 1. Validar campos obligatorios vacíos
        if (cliente.getNombre() == null || cliente.getApellido() == null ||
                cliente.getEmail() == null || cliente.getContraseña() == null ||
                cliente.getPais() == null || cliente.getProvincia() == null ||
                cliente.getLocalidad() == null || cliente.getCalle() == null ||
                cliente.getNumero() == null) {
            return ResponseEntity.badRequest().body("Error: Todos los campos obligatorios deben estar completos.");
        }

        // 2. Validar formato de email (simple)
        if (!cliente.getEmail().contains("@") || !cliente.getEmail().contains(".")) {
            return ResponseEntity.badRequest().body("Error: El formato del email no es válido.");
        }

        // 3. Validar longitud de contraseña (mínimo 8 caracteres)
        if (cliente.getContraseña().length() < 8) {
            return ResponseEntity.badRequest().body("Error: La contraseña debe tener al menos 8 caracteres.");
        }

        // 4. Verificar si el email ya existe en la BD
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: El email ya se encuentra registrado.");
        }

        // Si pasa todas las validaciones, guardamos
        Cliente nuevoCliente = clienteRepository.save(cliente);
        return ResponseEntity.ok(nuevoCliente);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        // 1. Buscamos al usuario. Spring ya sabe qué es getEmail() ahora.

        Cliente usuario = clienteRepository.findByEmail(datos.getEmail()).orElse(null);

        // 2. Si es null o la contraseña no va, el Handler hace su magia
        if (usuario == null || !usuario.getContraseña().equals(datos.getContraseña())) {
            throw new IllegalArgumentException("Email o contraseña incorrectos.");
        }
        return ResponseEntity.ok(usuario);
    }
}
