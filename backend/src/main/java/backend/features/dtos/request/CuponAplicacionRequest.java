package backend.features.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Solicitud para validar y aplicar un cupon al carrito")
public record CuponAplicacionRequest(
    @NotNull(message = "Debe especificar el ID del cliente")
    @Schema(description = "ID del cliente que usa el cupon", example = "1")
    Long clienteId,

    @NotBlank(message = "Debe ingresar el codigo del cupon")
    @Schema(description = "Codigo numerico del cupon", example = "12345678")
    String codigo,

    @NotEmpty(message = "El carrito no puede estar vacio")
    @Schema(description = "Items actuales del carrito")
    List<@Valid PedidoItemRequest> items
) {}
