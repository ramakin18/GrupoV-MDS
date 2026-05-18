package backend.features.services.interfaces.domain;

import backend.features.dtos.request.PedidoCreateRequest;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.models.SituacionPedido;

import java.util.List;

public interface IPedidoService {

    List<PedidoResponseDTO> getAll(String estado);

    PedidoResponseDTO getById(Long id);

    List<PedidoResponseDTO> getPendingDelivery();

    PedidoResponseDTO create(PedidoCreateRequest request);

    PedidoResponseDTO updateSituacion(Long id, SituacionPedido nuevaSituacion);

    PedidoResponseDTO cancelar(Long id, String motivo);
}
