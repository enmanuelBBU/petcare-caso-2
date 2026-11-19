package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Veterinario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findByActivoTrue();

    List<Veterinario> findByEspecialidadContainingIgnoreCase(String especialidad);

    List<Veterinario> findByActivoTrueAndEspecialidadContainingIgnoreCase(String especialidad);

    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    /** Para actualizar: el RUT/email no puede pertenecer a otro veterinario. */
    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
