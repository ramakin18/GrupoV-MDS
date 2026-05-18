package backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record KitCreateRequest(

    @NotBlank(message = "Debe ingresar un nombre valido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    String nombre,

    @NotBlank(message = "Debe ingresar una descripcion valida")
    String descripcion,

    @NotNull(message = "Debe ingresar un precio valido")
    @Positive(message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio no puede tener mas de 2 decimales")
    BigDecimal precio,

    Boolean activo,

    @NotEmpty(message = "El kit debe tener al menos un producto")
    List<@Valid KitProductoItem> productos
) {

    public boolean isActivo() {
        return activo == null || activo;
    }

    public record KitProductoItem(

        @NotNull(message = "Debe especificar el ID del producto")
        Long idProducto,

        @NotNull(message = "Debe especificar una cantidad")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad
    ) {}
}
