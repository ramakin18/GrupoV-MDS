package backend.features.services.interfaces.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;

public interface IProductoService {
    ProductoResponseDto create(ProductoCreateReqDto request, MultipartFile imagen);
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