package backend.features.services.interfaces.domain;

import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;

import java.math.BigDecimal;
import java.util.List;

public interface IProductoListService {

    List<ProductoResponseDto> execute(
            String nombre,
            BigDecimal precio,
            Integer stockMin,
            Integer stockMax,
            ProductoViewRole role,
            ProductoEstadoFiltro estado);
}
