package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Credenciales de inicio de sesión")
public class ClienteLoginRequestDto {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es valido")
    @Schema(description = "Email del cliente", example = "juan@example.com")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Schema(description = "Contraseña del cliente", example = "miPassword123")
    private String contrasena;
}
