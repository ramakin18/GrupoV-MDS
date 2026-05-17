package backend.features.dtos.response;

import backend.features.models.SituacionPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponseDTO(
        Long idPedido,
        Long clienteId,
        LocalDateTime fecha,
        SituacionPedido situacion,
        BigDecimal total
) {
}
