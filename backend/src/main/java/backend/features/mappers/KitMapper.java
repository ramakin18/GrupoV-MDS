package backend.features.mappers;

import backend.features.dtos.response.KitResponseDto;
import backend.features.models.Kit;
import backend.features.models.KitProducto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class KitMapper {

    public KitResponseDto toResponseDto(Kit kit) {
        List<KitResponseDto.KitProductoResponse> productos = kit.getProductos() != null
            ? kit.getProductos().stream().map(this::toProductoDto).toList()
            : Collections.emptyList();

        return new KitResponseDto(
            kit.getIdKit(),
            kit.getNombre(),
            kit.getDescripcion(),
            kit.getPrecio(),
            kit.getStock(),
            kit.isActivo(),
            productos
        );
    }

    public KitResponseDto.KitProductoResponse toProductoDto(KitProducto kp) {
        return new KitResponseDto.KitProductoResponse(
            kp.getId(),
            kp.getProducto().getIdProducto(),
            kp.getProducto().getNombreProducto(),
            kp.getCantidad()
        );
    }

    public List<KitResponseDto> toResponseDtoList(List<Kit> kits) {
        return kits.stream().map(this::toResponseDto).toList();
    }
}
