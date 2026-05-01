package backend.features.services.interfaces.domain;


import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;

public interface IProductoCreateService {

    ProductoResponseDto execute(ProductoCreateReqDto request);
}

