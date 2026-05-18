package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Item individual dentro del carrito")
public class CarritoItemRequestDto{
        @NotNull(message = "Debe ingresar el ID del producto")
        @Schema(description = "ID del producto", example = "1")
        private Long idProducto;

        @NotNull(message = "Debe ingresar la cantidad")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @Schema(description = "Cantidad del producto", example = "2")
        private Integer cantidad;

}
