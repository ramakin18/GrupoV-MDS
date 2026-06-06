package backend.features.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.Producto;

@Component
public class ProductoMapper {

    public Producto toModel(ProductoCreateReqDto request) {
        return Producto.builder()
                .nombreProducto(request.nombreProducto())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stockDisponible(request.stockDisponible())
                .stockMinimo(request.stockMinimo())
                .borrado(request.borrado() != null ? request.borrado() : false)
                .build();
    }

    public ProductoResponseDto toResponseDto(Producto model) {
        return new ProductoResponseDto(
                model.getIdProducto(),
                model.getNombreProducto(),
                model.getDescripcion(),
                model.getPrecio(),
                model.getStockDisponible(),
                model.getStockMinimo(),
                model.isBorrado(),
                model.getImagenUrl(),
                model.getPromedioPuntuacion(),
                model.getCantidadResenas()
        );
    }

    public List<ProductoResponseDto> toResponseDtoList(List<Producto> models) {
        return models.stream().map(this::toResponseDto).toList();
    }
}