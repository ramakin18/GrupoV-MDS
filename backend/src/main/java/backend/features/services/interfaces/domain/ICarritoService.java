package backend.features.services.interfaces.domain;

import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoResponseDto;

public interface ICarritoService {
    CarritoResponseDto validar(CarritoValidateRequestDto request);
}
