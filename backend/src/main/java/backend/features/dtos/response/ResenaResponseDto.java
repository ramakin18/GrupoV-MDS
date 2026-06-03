package backend.features.dtos.response;

import backend.features.models.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReseñaResponseDto {

    @Schema(description = "Id de reseña", example = "3")
    private long id;

    @Schema(description = "Número de estrellas", example = "1")
    private Integer puntuacion;

    @Schema(description = "Reseña", example = "Es un producto de alta calidad")
    private String descripcion;

    @Schema(description = "Fecha en la que se realizo la reseña")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Usuario que realizo la reseña")
    private String nombreUsuario;
}
