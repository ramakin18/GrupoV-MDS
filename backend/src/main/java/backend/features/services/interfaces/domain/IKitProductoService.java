package backend.features.services.interfaces.domain;

import backend.features.dtos.request.KitProductoCreateReqDto;
import backend.features.dtos.response.KitProductoResponseDto;

import java.util.List;

public interface IKitProductoService {

    KitProductoResponseDto create(KitProductoCreateReqDto request);

    List<KitProductoResponseDto> getAll();

    KitProductoResponseDto getById(Long id);

}