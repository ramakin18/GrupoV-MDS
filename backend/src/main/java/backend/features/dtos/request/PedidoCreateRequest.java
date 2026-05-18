package backend.features.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoCreateRequest(

    @NotNull(message = "Debe especificar el ID del cliente")
    Long clienteId,

    @NotEmpty(message = "El carrito no puede estar vacio")
    List<@Valid PedidoItemRequest> items,

    String formaPago

) {}
