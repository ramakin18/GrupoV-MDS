package backend.features.repositories;

import backend.features.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional; // Asegurate de que tenga este import arriba

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
}
