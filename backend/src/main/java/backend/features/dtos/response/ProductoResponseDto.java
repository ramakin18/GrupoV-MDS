package backend.features.dtos.response;

import java.math.BigDecimal;

public record ProductoResponseDto(

        Long idProducto,

        String nombreProducto,

        String descripcion,

        BigDecimal precio,

        Integer stockDisponible

) {
}
