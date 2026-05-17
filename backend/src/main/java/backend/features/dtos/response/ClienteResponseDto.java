package backend.features.dtos.response;

import backend.features.dtos.DomicilioEnvioDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private DomicilioEnvioDto domicilio;
    private String rol;
}
