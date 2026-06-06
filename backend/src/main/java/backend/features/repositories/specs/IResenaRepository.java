package backend.features.repositories.specs;

import backend.features.models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProducto_IdProductoAndEliminadoFalse(Long idProducto);

    List<Resena> findByKit_IdKitAndEliminadoFalse(Long kitId);

    List<Resena> findByEliminadoTrue();

    List<Resena> findByEliminadoFalse();

    boolean existsByUsuario_IdAndProducto_IdProductoAndEliminadoFalse(Long usuarioId, Long productoId);

    boolean existsByUsuario_IdAndKit_IdKitAndEliminadoFalse(Long usuarioId, Long kitId);

    Optional<Resena> findByIdAndEliminadoFalse(Long id);

    long countByProducto_IdProductoAndEliminadoFalse(Long productoId);

    long countByKit_IdKitAndEliminadoFalse(Long kitId);

    @Query("SELECT COALESCE(AVG(r.puntuacion), 0.0) FROM Resena r WHERE r.producto.idProducto = :productoId AND r.eliminado = false")
    Double avgPuntuacionByProductoId(Long productoId);

    @Query("SELECT COALESCE(AVG(r.puntuacion), 0.0) FROM Resena r WHERE r.kit.idKit = :kitId AND r.eliminado = false")
    Double avgPuntuacionByKitId(Long kitId);
}
