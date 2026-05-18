package backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomicilioUpdateRequest {

    @Valid
    private backend.features.dtos.DomicilioEnvioDto domicilio;
}
