package backend.features.services.interfaces.domain;

import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;


import java.util.List;

public interface IResenaService {

    ResenaResponseDto create(ResenaCreateRequestDto request);

    List<ResenaResponseDto> getAll();

    ResenaResponseDto getById(Long id);

    List<ResenaResponseDto> getByProductoId(Long productoId);


    void deleteByCliente(Long id, Long usuarioId);
    void deleteByAdmin(Long id);


}