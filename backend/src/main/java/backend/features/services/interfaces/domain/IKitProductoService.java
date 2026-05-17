package backend.features.services.interfaces.domain;

import backend.features.dtos.request.KitProductoCreateReqDto;
import backend.features.dtos.response.KitProductoResponseDto;

public interface IKitProductoService {

    KitProductoResponseDto create(KitProductoCreateReqDto request);

}