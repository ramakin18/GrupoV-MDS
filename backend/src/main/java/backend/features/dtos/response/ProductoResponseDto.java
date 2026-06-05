package backend.features.dtos.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con datos de un producto")
public record ProductoResponseDto(

        @Schema(description = "ID del producto", example = "1")
        Long idProducto,

        @Schema(description = "Nombre del producto", example = "Coca-Cola 500ml")
        String nombreProducto,

        @Schema(description = "Descripción del producto", example = "Gaseosa sabor cola 500ml")
        String descripcion,

        @Schema(description = "Precio del producto", example = "1500.00")
        BigDecimal precio,

        @Schema(description = "Stock disponible", example = "100")
        Integer stockDisponible,

        @Schema(description = "Stock mínimo", example = "10")
        Integer stockMinimo,

        @Schema(description = "Indica si el producto está borrado", example = "false")
        boolean borrado,

        @Schema(description = "URL de la imagen del producto", example = "https://res.cloudinary.com/...")
        String imagenUrl,

        @Schema(description = "Promedio de calificación", example = "4.5")
        Double promedioPuntuacion,

        @Schema(description = "Cantidad de reseñas", example = "12")
        Integer cantidadResenas
) {}