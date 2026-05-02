package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.InvalidCredentialsException;
import backend.exceptions.ResourceNotFoundException;
import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.mappers.ClienteMapper;
import backend.features.models.Cliente;
import backend.features.repositories.ClienteRepository;
import backend.features.services.interfaces.domain.IClienteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ClienteResponseDto> getAll() {
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDto getById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    public ClienteResponseDto update(Long id, ClienteCreateRequestDto request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (!cliente.getEmail().equals(request.getEmail())) {
            validateEmailNotRegistered(request.getEmail());
        }

        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        if (request.getContrasena() != null && !request.getContrasena().isEmpty()) {
            cliente.setContrasena(request.getContrasena());
        }
        cliente.setPais(request.getPais());
        cliente.setProvincia(request.getProvincia());
        cliente.setLocalidad(request.getLocalidad());
        cliente.setCalle(request.getCalle());
        cliente.setNumero(request.getNumero());
        cliente.setPiso(request.getPiso());
        cliente.setDepartamento(request.getDepartamento());

        Cliente updatedCliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(updatedCliente);
    }

    @Override
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
