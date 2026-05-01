package backend.features.mappers;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.Producto;

import java.util.List;

public class ProductoMapper {

    public static Producto toModel (ProductoCreateReqDto request) {
        return Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stockDisponible(request.stockDisponible())
                .build();
    }

    public static ProductoResponseDto toResponseDto (Producto model) {
        return new ProductoResponseDto(
                model.getIdProducto(),
                model.getNombreProducto(),
                model.getDescripcion(),
                model.getPrecio(),
                model.getStockDisponible()
        );
    }
    public static List<ProductoResponseDto> toResponseDtoList (List<Producto> models) {
        return models.stream()
                .map(ProductoMapper::toResponseDto)
                .toList();
    }

}