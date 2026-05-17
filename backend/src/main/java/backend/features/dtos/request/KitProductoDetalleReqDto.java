package backend.features.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record KitProductoDetalleReqDto(

        @NotNull
        Long idProducto,

        @NotNull
        @Positive
        Integer cantidad

) {
}