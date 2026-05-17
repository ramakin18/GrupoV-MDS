package backend.features.dtos.request;

import backend.features.models.SituacionPedido;
import jakarta.validation.constraints.NotNull;

public record PedidoSituacionUpdateRequestDto(

        @NotNull(message = "Debe ingresar una situacion valida")
        SituacionPedido situacion

) {
}