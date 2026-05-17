package backend.features.dtos.request;

import backend.features.models.EstadoKit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.List;

public record KitProductoCreateReqDto(

        @NotBlank
        String nombre,

        @NotBlank
        String descripcion,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal precio,

        @NotNull
        EstadoKit estado,

        @NotEmpty
        List<@Valid KitProductoDetalleReqDto> productos

) {
}