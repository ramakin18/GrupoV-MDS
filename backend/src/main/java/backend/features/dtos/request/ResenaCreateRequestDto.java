package backend.features.dtos.request;

import backend.features.models.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReseñaCreateRequestDto {

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Puntuación obligatoria")
    private Integer puntuacion;

    private String descripcion;

    private LocalDateTime fechaCreacion;

    private Long usuarioId;

    private Long productoId;
}
