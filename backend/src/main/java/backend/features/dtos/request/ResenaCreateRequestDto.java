package backend.features.dtos.request;

import backend.features.models.Cliente;
import backend.features.models.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime fechaCreacion;

    private Cliente usuario;

    private Producto producto;
}
