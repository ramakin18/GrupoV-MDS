package backend.features.dtos.response;

import backend.features.models.TipoDescuento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resultado de aplicar un cupon")
public record CuponAplicacionResponseDto(
    String codigo,
    TipoDescuento tipoDescuento,
    BigDecimal valor,
    BigDecimal subtotal,
    BigDecimal descuento,
    BigDecimal totalConDescuento,
    String fechaDesde,
    String fechaHasta
) {}
