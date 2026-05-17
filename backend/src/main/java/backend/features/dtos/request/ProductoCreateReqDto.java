package backend.features.dtos.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoCreateReqDto(

        @NotBlank
        @Size(min = 4, max = 50)
        String nombreProducto,

        @NotBlank
        String descripcion,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2, message = "Máximo 2 decimales") 
        BigDecimal precio,

        @NotNull
        @PositiveOrZero
        Integer stockDisponible,

        @NotNull
        @PositiveOrZero
        Integer stockMinimo,
        Boolean borrado
) {        
}
