package backend.features.repositories;

import backend.features.models.Pedido;
import backend.features.models.SituacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<Pedido> findBySituacionOrderByFechaDesc(SituacionPedido situacion);
    List<Pedido> findAllByOrderByFechaDesc();
    List<Pedido> findBySituacionInOrderByFechaDesc(List<SituacionPedido> situaciones);
}
