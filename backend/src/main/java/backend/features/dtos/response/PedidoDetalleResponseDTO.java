package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Detalle de un item dentro de un pedido")
public record PedidoDetalleResponseDTO(
    @Schema(description = "ID del detalle", example = "1")
    Long id,

    @Schema(description = "ID del producto", example = "1")
    Long idProducto,

    @Schema(description = "Nombre del producto", example = "Coca-Cola 500ml")
    String nombreProducto,

    @Schema(description = "Cantidad solicitada", example = "3")
    Integer cantidad,

    @Schema(description = "Precio unitario al momento del pedido", example = "1500.00")
    BigDecimal precioUnitario,

    @Schema(description = "Subtotal del item", example = "4500.00")
    BigDecimal subtotal
) {}
