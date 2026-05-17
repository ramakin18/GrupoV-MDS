package backend.features.repositories;

import backend.features.models.KitProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitProductoRepository extends JpaRepository<KitProducto, Long> {

    boolean existsByNombre(String nombre);

}
