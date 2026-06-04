package backend.features.dtos.response;

import backend.features.models.TipoDescuento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Cupon de descuento generado")
public record CuponResponseDto(
    Long idCupon,
    String codigo,
    TipoDescuento tipoDescuento,
    BigDecimal valor,
    String fechaDesde,
    String fechaHasta,
    List<CuponClienteResponseDto> clientes,
    List<CuponProductoResponseDto> productos,
    int mailsEnviados
) {}
