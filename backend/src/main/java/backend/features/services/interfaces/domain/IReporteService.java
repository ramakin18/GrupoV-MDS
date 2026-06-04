package backend.features.services.interfaces.domain;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.dtos.response.StockMinimoReporteResponseDto;
import java.util.List;

public interface IReporteService {
    List<ProductoMasVendidoResponseDto> getProductosMasVendidos(Integer mes, Integer anio, Integer dia);
    List<StockMinimoReporteResponseDto> getProductosCercaStockMinimo();
}
