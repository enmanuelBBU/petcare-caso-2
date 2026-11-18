package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Cliente;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRut(String rut);

    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    /** Para actualizar: el RUT no puede pertenecer a otro cliente. */
    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
