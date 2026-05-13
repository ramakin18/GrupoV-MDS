package backend.features.services.interfaces.domain;

import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.ProductoViewRole;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoListService {

    List<ProductoResponseDto> execute(String nombre, BigDecimal precio, Integer stock, ProductoViewRole role);
}
