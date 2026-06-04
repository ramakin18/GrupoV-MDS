package backend.features.dtos.response;

import backend.features.models.SituacionPedido;
import backend.features.dtos.DomicilioEnvioDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Respuesta con datos de un pedido")
public record PedidoResponseDTO(
    @Schema(description = "ID del pedido", example = "1")
    Long idPedido,

    @Schema(description = "ID del cliente", example = "1")
    Long clienteId,

    @Schema(description = "Nombre del cliente", example = "Juan")
    String nombreCliente,

    @Schema(description = "Apellido del cliente", example = "Pérez")
    String apellidoCliente,

    @Schema(description = "Email del cliente", example = "juan@example.com")
    String emailCliente,

    @Schema(description = "Fecha de creación del pedido")
    LocalDateTime fecha,

    @Schema(description = "Fecha de última actualización")
    LocalDateTime fechaActualizacion,

    @Schema(description = "Situación actual del pedido", example = "PENDIENTE")
    SituacionPedido situacion,

    @Schema(description = "Motivo de cancelación (si aplica)", example = "Solicitud del cliente")
    String motivoCancelacion,

    @Schema(description = "Forma de pago", example = "EFECTIVO")
    String formaPago,

    @Schema(description = "Total del pedido", example = "4500.00")
    BigDecimal total,

    @Schema(description = "Subtotal antes de aplicar descuentos", example = "5000.00")
    BigDecimal subtotal,

    @Schema(description = "Descuento aplicado", example = "500.00")
    BigDecimal descuento,

    @Schema(description = "Codigo de cupon aplicado", example = "12345678")
    String codigoCupon,

    @Schema(description = "Dirección de envío")
    DomicilioEnvioDto domicilioEnvio,

    @Schema(description = "Detalles del pedido")
    List<PedidoDetalleResponseDTO> detalles
) {}
