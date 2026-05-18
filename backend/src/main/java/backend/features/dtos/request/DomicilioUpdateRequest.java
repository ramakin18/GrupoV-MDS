package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud para actualizar el domicilio de un cliente")
public class DomicilioUpdateRequest {

    @Valid
    @Schema(description = "Nuevo domicilio del cliente")
    private backend.features.dtos.DomicilioEnvioDto domicilio;
}
