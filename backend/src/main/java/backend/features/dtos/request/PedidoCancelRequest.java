package backend.features.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record PedidoCancelRequest(

    @NotBlank(message = "Debe ingresar un motivo de cancelacion")
    String motivo

) {}
