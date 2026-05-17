package backend.features.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import backend.features.models.Producto;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
    List<Producto> findByBorradoFalse();
    Optional<Producto> findByIdProductoAndBorradoFalse(Long id);
    boolean existsByNombreProductoIgnoreCase(String nombreProducto);
    boolean existsByNombreProductoIgnoreCaseAndIdProductoNot(String nombreProducto, Long idProducto);
}