package backend.features.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoCreateReqDto(

        @NotBlank
        @Size(min = 4, max = 50)
        String nombreProducto,

        @NotBlank
        String descripcion,

        @NotNull
        @Positive
        BigDecimal precio,

        @NotNull
        @Positive
        Integer stockDisponible

) {




}
