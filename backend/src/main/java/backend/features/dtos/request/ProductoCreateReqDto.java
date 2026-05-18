package backend.features.dtos.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear o actualizar un producto")
public record ProductoCreateReqDto(

        @NotBlank
        @Size(min = 4, max = 50)
        @Schema(description = "Nombre del producto", example = "Coca-Cola 500ml")
        String nombreProducto,

        @NotBlank
        @Schema(description = "Descripción del producto", example = "Gaseosa sabor cola 500ml")
        String descripcion,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2, message = "Máximo 2 decimales")
        @Schema(description = "Precio del producto", example = "1500.00")
        BigDecimal precio,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Stock disponible", example = "100")
        Integer stockDisponible,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Stock mínimo", example = "10")
        Integer stockMinimo,

        @Schema(description = "Indica si el producto está borrado (soft delete)", example = "false")
        Boolean borrado
) {        
}
