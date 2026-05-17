package backend.features.dtos.response;

import backend.features.models.EstadoKit;

import java.math.BigDecimal;
import java.util.List;

public record KitProductoResponseDto(

        Long idKit,

        String nombre,

        String descripcion,

        BigDecimal precio,

        Integer stock,

        EstadoKit estado,

        List<KitProductoDetalleResponseDto> productos

) {
}