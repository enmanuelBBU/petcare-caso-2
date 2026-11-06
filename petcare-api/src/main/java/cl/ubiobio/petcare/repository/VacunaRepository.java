package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Vacuna;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Long> {

    List<Vacuna> findByMascotaIdOrderByFechaAplicacionDesc(Long mascotaId);

    /** Vacunas cuya proxima dosis vence entre dos fechas (para recordatorios). */
    List<Vacuna> findByFechaProximaDosisBetween(LocalDate desde, LocalDate hasta);
}
