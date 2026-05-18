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
import org.springframework.transaction.annotation.Transactional;

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
        cliente.setPais(request.getDomicilio().getPais());
        cliente.setProvincia(request.getDomicilio().getProvincia());
        cliente.setLocalidad(request.getDomicilio().getLocalidad());
        cliente.setCalle(request.getDomicilio().getCalle());
        cliente.setNumero(request.getDomicilio().getNumero());
        cliente.setPiso(request.getDomicilio().getPiso());
        cliente.setDepartamento(request.getDomicilio().getDepartamento());

        Cliente updatedCliente = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(updatedCliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto updateDomicilio(Long id, backend.features.dtos.DomicilioEnvioDto domicilio) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        cliente.setPais(domicilio.getPais());
        cliente.setProvincia(domicilio.getProvincia());
        cliente.setLocalidad(domicilio.getLocalidad());
        cliente.setCalle(domicilio.getCalle());
        cliente.setNumero(domicilio.getNumero());
        cliente.setPiso(domicilio.getPiso());
        cliente.setDepartamento(domicilio.getDepartamento());

        Cliente updated = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

}
