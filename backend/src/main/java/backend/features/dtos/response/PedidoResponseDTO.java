package backend.features.dtos.response;

import backend.features.models.SituacionPedido;
import backend.features.dtos.DomicilioEnvioDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
    Long idPedido,
    Long clienteId,
    String nombreCliente,
    String apellidoCliente,
    String emailCliente,
    LocalDateTime fecha,
    LocalDateTime fechaActualizacion,
    SituacionPedido situacion,
    String motivoCancelacion,
    String formaPago,
    BigDecimal total,
    DomicilioEnvioDto domicilioEnvio,
    List<PedidoDetalleResponseDTO> detalles
) {}
