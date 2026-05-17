package backend.features.dtos.response;

public record KitProductoDetalleResponseDto(

        Long idProducto,

        String nombreProducto,

        Integer cantidad

) {
}