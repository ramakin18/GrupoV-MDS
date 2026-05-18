package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item del carrito validado")
public class CarritoItemResponseDto{
       @Schema(description = "ID del producto", example = "1")
       private Long idProducto;

       @Schema(description = "Nombre del producto", example = "Coca-Cola 500ml")
       private String nombreProducto;

       @Schema(description = "Precio unitario", example = "1500.00")
       private BigDecimal precioUnitario;

       @Schema(description = "Cantidad solicitada", example = "2")
       private Integer cantidad;

       @Schema(description = "Subtotal del item", example = "3000.00")
       private BigDecimal subtotal;
}
