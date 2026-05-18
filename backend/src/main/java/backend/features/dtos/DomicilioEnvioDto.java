package backend.features.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dirección de envío")
public class DomicilioEnvioDto {

    @NotBlank(message = "El pais es obligatorio")
    @Schema(description = "País", example = "Argentina")
    private String pais;

    @NotBlank(message = "La provincia es obligatoria")
    @Schema(description = "Provincia", example = "Buenos Aires")
    private String provincia;

    @NotBlank(message = "La localidad es obligatoria")
    @Schema(description = "Localidad", example = "La Plata")
    private String localidad;

    @NotBlank(message = "La calle es obligatoria")
    @Schema(description = "Calle", example = "Calle 7")
    private String calle;

    @NotBlank(message = "El numero es obligatorio")
    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Piso (opcional)", example = "3")
    private String piso;

    @Schema(description = "Departamento (opcional)", example = "A")
    private String departamento;
}
