package backend.features.dtos.request;

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
public class CarritoValidateRequestDto{
        @NotEmpty(message = "El carrito no puede estar vacio")
        private List<@Valid CarritoItemRequestDto> items;

}
