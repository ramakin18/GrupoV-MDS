package backend.features.repositories;

import backend.features.models.Pedido;
import backend.features.models.SituacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findBySituacion(SituacionPedido situacion);
}
