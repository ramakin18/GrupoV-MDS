package backend.features.mappers;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.Producto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductoMapper {

    public Producto toModel(ProductoCreateReqDto request) {
        return Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stockDisponible(request.stockDisponible())
                .build();
    }

    public ProductoResponseDto toResponseDto(Producto model) {
        return new ProductoResponseDto(
                model.getIdProducto(),
                model.getNombreProducto(),
                model.getDescripcion(),
                model.getPrecio(),
                model.getStockDisponible(),
                model.isBorrado()
        );
    }

    public List<ProductoResponseDto> toResponseDtoList(List<Producto> models) {
        return models.stream()
                .map(this::toResponseDto)
                .toList();
    }
}
