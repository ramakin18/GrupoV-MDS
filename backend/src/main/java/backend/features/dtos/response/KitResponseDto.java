package backend.features.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record KitResponseDto(

    Long idKit,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    boolean activo,
    List<KitProductoResponse> productos
) {
    public record KitProductoResponse(
        Long id,
        Long idProducto,
        String nombreProducto,
        Integer cantidad
    ) {}
}
