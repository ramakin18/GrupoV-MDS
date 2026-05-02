package backend.features.services.interfaces.domain;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {
    ProductoResponseDto create(ProductoCreateReqDto request);
    List<ProductoResponseDto> getAll(String nombre, BigDecimal precio, Integer stock);
    ProductoResponseDto getById(Long id);
    ProductoResponseDto update(Long id, ProductoCreateReqDto request);
    void delete(Long id);
}
