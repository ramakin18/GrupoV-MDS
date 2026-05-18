package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta con la validación del carrito")
public class CarritoResponseDto {
    @Schema(description = "Items del carrito validados")
    private List<CarritoItemResponseDto> items;

    @Schema(description = "Total del carrito", example = "4500.00")
    private BigDecimal total;
}
