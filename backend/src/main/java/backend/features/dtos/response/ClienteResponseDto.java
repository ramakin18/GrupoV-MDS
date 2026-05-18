package backend.features.dtos.response;

import backend.features.dtos.DomicilioEnvioDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con datos de un cliente")
public class ClienteResponseDto {

    @Schema(description = "ID del cliente", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @Schema(description = "Email del cliente", example = "juan@example.com")
    private String email;

    @Schema(description = "Domicilio de envío del cliente")
    private DomicilioEnvioDto domicilio;

    @Schema(description = "Rol del cliente", example = "USUARIO")
    private String rol;
}
