package backend.features.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioEnvioDto {

    @NotBlank(message = "El pais es obligatorio")
    private String pais;

    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    @NotBlank(message = "La localidad es obligatoria")
    private String localidad;

    @NotBlank(message = "La calle es obligatoria")
    private String calle;

    @NotBlank(message = "El numero es obligatorio")
    private String numero;

    private String piso;
    private String departamento;
}
