package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Motivo de cancelación de un pedido")
public record PedidoCancelRequest(

    @NotBlank(message = "Debe ingresar un motivo de cancelacion")
    @Schema(description = "Motivo de la cancelación", example = "El cliente ya no necesita el pedido")
    String motivo

) {}
