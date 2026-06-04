package backend.features.repositories;

import backend.features.models.CuponCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuponClienteRepository extends JpaRepository<CuponCliente, Long> {
    Optional<CuponCliente> findByCuponCodigoAndClienteId(String codigo, Long clienteId);
}
