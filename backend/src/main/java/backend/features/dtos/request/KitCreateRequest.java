package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Datos para crear o actualizar un kit")
public record KitCreateRequest(

    @NotBlank(message = "Debe ingresar un nombre valido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre del kit", example = "Combo Hamburguesa")
    String nombre,

    @NotBlank(message = "Debe ingresar una descripcion valida")
    @Schema(description = "Descripción del kit", example = "Hamburguesa con papas y gaseosa")
    String descripcion,

    @NotNull(message = "Debe ingresar un precio valido")
    @Positive(message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio no puede tener mas de 2 decimales")
    @Schema(description = "Precio del kit", example = "4500.00")
    BigDecimal precio,

    @Schema(description = "Indica si el kit está activo", example = "true")
    Boolean activo,

    @NotEmpty(message = "El kit debe tener al menos un producto")
    @Schema(description = "Productos que componen el kit")
    List<@Valid KitProductoItem> productos
) {

    public boolean isActivo() {
        return activo == null || activo;
    }

    @Schema(description = "Producto dentro de un kit con su cantidad")
    public record KitProductoItem(

        @NotNull(message = "Debe especificar el ID del producto")
        @Schema(description = "ID del producto", example = "1")
        Long idProducto,

        @NotNull(message = "Debe especificar una cantidad")
        @Positive(message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Cantidad del producto en el kit", example = "2")
        Integer cantidad
    ) {}
}
