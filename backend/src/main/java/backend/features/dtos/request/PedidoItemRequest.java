package backend.features.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequest(

    @NotNull(message = "Debe especificar el ID del producto")
    Long idProducto,

    @NotNull(message = "Debe especificar la cantidad")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    Integer cantidad

) {}
