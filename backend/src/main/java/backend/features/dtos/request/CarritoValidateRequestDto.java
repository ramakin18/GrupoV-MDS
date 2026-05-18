package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de validación del carrito de compras")
public class CarritoValidateRequestDto{
        @NotEmpty(message = "El carrito no puede estar vacio")
        @Schema(description = "Items del carrito a validar")
        private List<@Valid CarritoItemRequestDto> items;

}
