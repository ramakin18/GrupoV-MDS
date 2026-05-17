package backend.features.services.interfaces.domain;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoService {
    ProductoResponseDto create(ProductoCreateReqDto request);
    List<ProductoResponseDto> getAll(
            String nombre,
            BigDecimal precio,
            Integer stockMin,
            Integer stockMax,
            ProductoViewRole role,
            ProductoEstadoFiltro estado);
    ProductoResponseDto getById(Long id);
    ProductoResponseDto update(Long id, ProductoCreateReqDto request);
    void delete(Long id);
}
