package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto abarcado por un cupon")
public record CuponProductoResponseDto(
    Long idProducto,
    String nombreProducto
) {}
