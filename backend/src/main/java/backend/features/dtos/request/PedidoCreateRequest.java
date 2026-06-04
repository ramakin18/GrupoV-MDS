package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Datos para crear un nuevo pedido")
public record PedidoCreateRequest(

    @NotNull(message = "Debe especificar el ID del cliente")
    @Schema(description = "ID del cliente que realiza el pedido", example = "1")
    Long clienteId,

    @NotEmpty(message = "El carrito no puede estar vacio")
    @Schema(description = "Items del pedido")
    List<@Valid PedidoItemRequest> items,

    @Schema(description = "Forma de pago", example = "EFECTIVO")
    String formaPago,

    @Schema(description = "Codigo de cupon a aplicar", example = "12345678")
    String codigoCupon

) {}
