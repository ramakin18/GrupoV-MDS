package backend.features.dtos.response;

import java.math.BigDecimal;

public record PedidoDetalleResponseDTO(
    Long id,
    Long idProducto,
    String nombreProducto,
    Integer cantidad,
    BigDecimal precioUnitario,
    BigDecimal subtotal
) {}
