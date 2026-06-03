package backend.features.mappers;


import backend.features.dtos.request.ReseñaCreateRequestDto;
import backend.features.dtos.response.ReseñaResponseDto;
import backend.features.models.Reseña;

public class ReseñaMapper {
    public Reseña toModel(ReseñaCreateRequestDto request) {
        return Reseña.builder()
                .puntuacion(request.getPuntuacion())
                .descripcion(request.getDescripcion())
                .usuario(request.getUsuarioId())
                .fechaCreacion(request.getFechaCreacion())
                .build();
    }
    public ReseñaResponseDto toResponseDto(Reseña model) {
        return ReseñaResponseDto.builder()
                .id(model.getId())
                .puntuacion(model.getPuntuacion())
                .descripcion(model.getDescripcion())
                .fechaCreacion(model.getFechaCreacion())
                .usuario(model.getUsuario())
                .build();
    }
}
