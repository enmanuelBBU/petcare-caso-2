package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.HorarioVeterinario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioVeterinarioRepository extends JpaRepository<HorarioVeterinario, Long> {

    List<HorarioVeterinario> findByVeterinarioId(Long veterinarioId);

    List<HorarioVeterinario> findByVeterinarioIdAndDiaSemana(Long veterinarioId, Integer diaSemana);
}
