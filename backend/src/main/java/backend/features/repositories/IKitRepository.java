package backend.features.repositories;

import backend.features.models.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IKitRepository extends JpaRepository<Kit, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdKitNot(String nombre, Long idKit);
    List<Kit> findAllByOrderByNombreAsc();
    Optional<Kit> findByIdKitAndActivoTrue(Long idKit);
    List<Kit> findByActivoTrueOrderByNombreAsc();
    boolean existsByProductos_Producto_IdProductoAndActivoTrue(Long idProducto);
    List<Kit> findByProductos_Producto_IdProducto(Long idProducto);
}
