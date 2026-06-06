package backend.features.services.interfaces.domain;

import java.util.List;

import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;

public interface IResenaService {
    ResenaResponseDto create(ResenaCreateRequestDto request);
    ResenaResponseDto update(Long id, ResenaCreateRequestDto request, Long usuarioId);
    List<ResenaResponseDto> getAll();
    ResenaResponseDto getById(Long id);
    List<ResenaResponseDto> getByProductoId(Long productoId);
    void deleteByCliente(Long id, Long usuarioId);
    void deleteByAdmin(Long id);
}