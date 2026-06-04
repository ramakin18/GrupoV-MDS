package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cliente destinatario de un cupon")
public record CuponClienteResponseDto(
    Long id,
    String nombre,
    String apellido,
    String email,
    boolean usado
) {}
