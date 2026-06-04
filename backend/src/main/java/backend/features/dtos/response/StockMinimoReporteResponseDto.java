package backend.features.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto o kit cercano al stock minimo")
public record StockMinimoReporteResponseDto(

    @Schema(description = "Codigo visible del producto o kit", example = "PROD-1")
    String codigo,

    @Schema(description = "Nombre del producto o kit", example = "Coca-Cola 500ml")
    String nombre,

    @Schema(description = "Stock actual", example = "8")
    Integer stockActual,

    @Schema(description = "Stock minimo", example = "10")
    Integer stockMinimo
) {}
