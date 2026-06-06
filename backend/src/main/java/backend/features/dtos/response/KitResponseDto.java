package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Respuesta con datos de un kit")
public record KitResponseDto(

    @Schema(description = "ID del kit", example = "1")
    Long idKit,

    @Schema(description = "Nombre del kit", example = "Combo Hamburguesa")
    String nombre,

    @Schema(description = "Descripción del kit", example = "Hamburguesa con papas y gaseosa")
    String descripcion,

    @Schema(description = "Precio del kit", example = "4500.00")
    BigDecimal precio,

    @Schema(description = "Stock disponible del kit", example = "50")
    Integer stock,

    @Schema(description = "Indica si el kit está activo", example = "true")
    boolean activo,

    @Schema(description = "Productos que componen el kit")
    List<KitProductoResponse> productos,

    @Schema(description = "Puntuacion promedio del kit", example = "4.2")
    Double promedioPuntuacion,

    @Schema(description = "Cantidad de reseñas", example = "15")
    Integer cantidadResenas
) {
    @Schema(description = "Producto dentro de un kit")
    public record KitProductoResponse(
        @Schema(description = "ID de la relación", example = "1")
        Long id,

        @Schema(description = "ID del producto", example = "1")
        Long idProducto,

        @Schema(description = "Nombre del producto", example = "Coca-Cola 500ml")
        String nombreProducto,

        @Schema(description = "Cantidad incluida en el kit", example = "2")
        Integer cantidad
    ) {}
}
