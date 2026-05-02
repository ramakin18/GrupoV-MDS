package backend.features.services.interfaces.domain;

import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.response.ClienteResponseDto;

public interface IClienteService {
    ClienteResponseDto register(ClienteCreateRequestDto request);
    ClienteResponseDto login(ClienteLoginRequestDto request);
    List<ClienteResponseDto> getAll();
    ClienteResponseDto getById(Long id);
    ClienteResponseDto update(Long id, ClienteCreateRequestDto request);
    void delete(Long id);
}
