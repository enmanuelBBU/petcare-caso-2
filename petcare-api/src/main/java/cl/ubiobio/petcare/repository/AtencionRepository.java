package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Atencion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    List<Atencion> findByMascotaIdOrderByFechaAtencionDesc(Long mascotaId);

    Optional<Atencion> findByReservaId(Long reservaId);
}
