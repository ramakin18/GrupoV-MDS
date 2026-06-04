package backend.features.repositories;

import backend.features.models.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {
    Optional<Cupon> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Cupon> findAllByOrderByFechaCreacionDesc();
}
