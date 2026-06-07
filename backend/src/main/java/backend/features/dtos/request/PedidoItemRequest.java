package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Item individual dentro de un pedido")
public record PedidoItemRequest(

    @NotNull(message = "Debe especificar el ID del producto")
    @Schema(description = "ID del producto", example = "1")
    Long idProducto,

    @NotNull(message = "Debe especificar la cantidad")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad del producto", example = "3")
    Integer cantidad,

    @Schema(description = "Precio unitario opcional (para items expandidos de kits)")
    BigDecimal precioUnitario

) {}
