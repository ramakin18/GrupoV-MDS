package backend.features.mappers;

import backend.features.dtos.DomicilioEnvioDto;
import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.models.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteCreateRequestDto dto) {
        return Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .contrasena(dto.getContrasena())
                .pais(dto.getDomicilio().getPais())
                .provincia(dto.getDomicilio().getProvincia())
                .localidad(dto.getDomicilio().getLocalidad())
                .calle(dto.getDomicilio().getCalle())
                .numero(dto.getDomicilio().getNumero())
                .piso(dto.getDomicilio().getPiso())
                .departamento(dto.getDomicilio().getDepartamento())
                .rol(dto.getRol())
                .build();
    }

    public ClienteResponseDto toResponseDto(Cliente cliente) {
        DomicilioEnvioDto domicilio = DomicilioEnvioDto.builder()
                .pais(cliente.getPais())
                .provincia(cliente.getProvincia())
                .localidad(cliente.getLocalidad())
                .calle(cliente.getCalle())
                .numero(cliente.getNumero())
                .piso(cliente.getPiso())
                .departamento(cliente.getDepartamento())
                .build();

        return ClienteResponseDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .domicilio(domicilio)
                .rol(cliente.getRol())
                .build();
    }
}
