package backend.features.dtos.request;

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
public class CarritoItemRequestDto{
        @NotNull(message = "Debe ingresar el ID del producto")
        private Long idProducto;

        @NotNull(message = "Debe ingresar la cantidad")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

}
