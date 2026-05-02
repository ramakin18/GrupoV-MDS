package backend.features.mappers;

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
                .pais(dto.getPais())
                .provincia(dto.getProvincia())
                .localidad(dto.getLocalidad())
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .piso(dto.getPiso())
                .departamento(dto.getDepartamento())
                .rol(dto.getRol())
                .build();
    }

    public ClienteResponseDto toResponseDto(Cliente cliente) {
        return ClienteResponseDto.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .pais(cliente.getPais())
                .provincia(cliente.getProvincia())
                .localidad(cliente.getLocalidad())
                .calle(cliente.getCalle())
                .numero(cliente.getNumero())
                .piso(cliente.getPiso())
                .departamento(cliente.getDepartamento())
                .rol(cliente.getRol())
                .build();
    }
}
