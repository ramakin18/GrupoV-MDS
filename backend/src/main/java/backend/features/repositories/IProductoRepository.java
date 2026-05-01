package backend.features.repositories;

import backend.features.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    List<Producto> findByBorradoFalse();
    Optional<Producto> findByIdProductoAndBorradoFalse(Long id);

}