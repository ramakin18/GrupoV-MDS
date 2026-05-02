package backend.features.dtos.response;

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
    private String pais;
    private String provincia;
    private String localidad;
    private String calle;
    private String numero;
    private String piso;
    private String departamento;
    private String rol;
}
