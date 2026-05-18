package backend.features.dtos.request;

import backend.features.models.SituacionPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud para actualizar la situación de un pedido")
public record PedidoSituacionUpdateRequestDto(

        @NotNull(message = "Debe ingresar una situacion valida")
        @Schema(description = "Nueva situación del pedido", example = "LISTO")
        SituacionPedido situacion

) {
}