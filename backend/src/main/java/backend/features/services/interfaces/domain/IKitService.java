package backend.features.services.interfaces.domain;

import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;

import java.util.List;

public interface IKitService {
    KitResponseDto create(KitCreateRequest request);
    List<KitResponseDto> getAll();
    List<KitResponseDto> getActivos();
    KitResponseDto getById(Long id);
    KitResponseDto update(Long id, KitCreateRequest request);
    void delete(Long id);
}
