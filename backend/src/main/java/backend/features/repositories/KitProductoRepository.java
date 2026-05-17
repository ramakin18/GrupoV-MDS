package backend.features.repositories;

import backend.features.models.EstadoKit;
import backend.features.models.KitProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KitProductoRepository extends JpaRepository<KitProducto, Long> {

    boolean existsByNombre(String nombre);

    List<KitProducto> findByEstado(EstadoKit estado);

}
