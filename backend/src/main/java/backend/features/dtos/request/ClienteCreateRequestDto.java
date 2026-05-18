package backend.features.dtos.request;

import backend.features.dtos.DomicilioEnvioDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para registrar un nuevo cliente")
public class ClienteCreateRequestDto {

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es valido")
    @Schema(description = "Email del cliente", example = "juan@example.com")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña del cliente (mín. 8 caracteres)", example = "miPassword123")
    private String contrasena;

    @Valid
    @NotNull(message = "El domicilio de envio es obligatorio")
    @Schema(description = "Domicilio de envío del cliente")
    private DomicilioEnvioDto domicilio;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(description = "Rol del cliente", example = "USUARIO")
    private String rol;
}
