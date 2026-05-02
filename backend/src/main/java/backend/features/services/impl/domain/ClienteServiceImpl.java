package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.InvalidCredentialsException;
import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.mappers.ClienteMapper;
import backend.features.models.Cliente;
import backend.features.repositories.ClienteRepository;
import backend.features.services.interfaces.domain.IClienteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public ClienteResponseDto register(ClienteCreateRequestDto request) {
        validateEmailNotRegistered(request.getEmail());

        Cliente cliente = clienteMapper.toEntity(request);
        Cliente savedCliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(savedCliente);
    }

    @Override
    public ClienteResponseDto login(ClienteLoginRequestDto request) {
        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email o contrasena incorrectos."));

        if (!cliente.validatePassword(request.getContrasena())) {
            throw new InvalidCredentialsException("Email o contrasena incorrectos.");
        }

        return clienteMapper.toResponseDto(cliente);
    }

    private void validateEmailNotRegistered(String email) {
        if (clienteRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("El email ya se encuentra registrado.");
        }
    }
}
