package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResenaCreateRequestDto {

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Puntuación obligatoria")
    private Integer puntuacion;

    @Size(max = 500)
    private String descripcion;

    @Schema(description = "ID del usuario")
    @NotNull
    private Long usuarioId;

    @Schema(description = "ID del producto (opcional si se especifica kitId)")
    private Long productoId;

    @Schema(description = "ID del kit (opcional si se especifica productoId)")
    private Long kitId;
}
