package backend.features.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarritoItemResponseDto{
       private Long idProducto;
       private String nombreProducto;
       private BigDecimal precioUnitario;
       private Integer cantidad;
       private BigDecimal subtotal;
}
