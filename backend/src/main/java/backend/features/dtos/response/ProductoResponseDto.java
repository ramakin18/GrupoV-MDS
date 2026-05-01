package backend.features.dtos.response;

import java.math.BigDecimal;

public record ProductoResponseDto(

        Long id,

        String nombre,

        String descripcion,

        BigDecimal precio,

        Integer stockDisponible

) {


}
