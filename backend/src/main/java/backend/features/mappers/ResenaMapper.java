package backend.features.mappers;

import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;
import backend.features.models.Resena;
import org.springframework.stereotype.Component;

@Component
public class ResenaMapper {
    public Resena toModel(ResenaCreateRequestDto request) {
        return Resena.builder()
                .puntuacion(request.getPuntuacion())
                .descripcion(request.getDescripcion())
                .build();
    }

    public ResenaResponseDto toResponseDto(Resena model) {
        return ResenaResponseDto.builder()
                .id(model.getId())
                .puntuacion(model.getPuntuacion())
                .descripcion(model.getDescripcion())
                .fechaCreacion(model.getFechaCreacion())
                .nombreUsuario(model.getUsuario().getNombre())
                .eliminado(model.getEliminado() != null && model.getEliminado())
                .productoId(model.getProducto() != null ? model.getProducto().getIdProducto() : null)
                .kitId(model.getKit() != null ? model.getKit().getIdKit() : null)
                .build();
    }
}
