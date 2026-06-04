package backend.features.dtos.request;

import backend.features.models.TipoDescuento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Datos para generar un cupon de descuento")
public record CuponCreateRequest(
    @NotEmpty(message = "Debe seleccionar al menos un cliente")
    @Schema(description = "Clientes destinatarios del cupon")
    List<Long> clienteIds,

    @Schema(description = "Productos abarcados. Si se omite, aplica a todos los productos")
    List<Long> productoIds,

    @NotNull(message = "Debe seleccionar el tipo de descuento")
    @Schema(description = "Tipo de descuento", example = "PORCENTAJE")
    TipoDescuento tipoDescuento,

    @NotNull(message = "Debe ingresar el valor del descuento")
    @Positive(message = "El descuento debe ser mayor a cero")
    @Schema(description = "Monto o porcentaje del descuento", example = "15.50")
    BigDecimal valor,

    @NotNull(message = "Debe ingresar la fecha desde")
    @Schema(description = "Fecha desde en formato dd/mm/aaaa", example = "01/06/2026")
    String fechaDesde,

    @NotNull(message = "Debe ingresar la fecha hasta")
    @Schema(description = "Fecha hasta en formato dd/mm/aaaa", example = "30/06/2026")
    String fechaHasta
) {}
