package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByClienteId(Long clienteId);
}
