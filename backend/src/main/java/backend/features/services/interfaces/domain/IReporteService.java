package backend.features.services.interfaces.domain;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import java.util.List;

public interface IReporteService {
    List<ProductoMasVendidoResponseDto> getProductosMasVendidos(Integer mes, Integer anio);
}
