package backend.features.repositories.specs;


import backend.features.models.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProducto_IdProducto(Long idProducto);

    boolean existsByUsuario_IdAndProducto_IdProducto(Long usuarioId, Long productoId);
}