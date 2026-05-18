package backend.features.mappers;

import backend.features.dtos.DomicilioEnvioDto;
import backend.features.dtos.response.PedidoDetalleResponseDTO;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.models.Pedido;
import backend.features.models.PedidoDetalle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {

    public PedidoResponseDTO toResponseDto(Pedido pedido) {
        DomicilioEnvioDto domicilio = DomicilioEnvioDto.builder()
            .pais(pedido.getPaisEnvio())
            .provincia(pedido.getProvinciaEnvio())
            .localidad(pedido.getLocalidadEnvio())
            .calle(pedido.getCalleEnvio())
            .numero(pedido.getNumeroEnvio())
            .piso(pedido.getPisoEnvio())
            .departamento(pedido.getDepartamentoEnvio())
            .build();

        List<PedidoDetalleResponseDTO> detalles = pedido.getDetalles() != null
            ? pedido.getDetalles().stream().map(this::toDetalleDto).toList()
            : List.of();

        return new PedidoResponseDTO(
            pedido.getIdPedido(),
            pedido.getCliente().getId(),
            pedido.getCliente().getNombre(),
            pedido.getCliente().getApellido(),
            pedido.getCliente().getEmail(),
            pedido.getFecha(),
            pedido.getFechaActualizacion(),
            pedido.getSituacion(),
            pedido.getMotivoCancelacion(),
            pedido.getFormaPago(),
            pedido.getTotal(),
            domicilio,
            detalles
        );
    }

    public PedidoDetalleResponseDTO toDetalleDto(PedidoDetalle detalle) {
        return new PedidoDetalleResponseDTO(
            detalle.getId(),
            detalle.getProducto().getIdProducto(),
            detalle.getProducto().getNombreProducto(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal()
        );
    }

    public List<PedidoResponseDTO> toResponseDtoList(List<Pedido> pedidos) {
        return pedidos.stream().map(this::toResponseDto).toList();
    }
}
