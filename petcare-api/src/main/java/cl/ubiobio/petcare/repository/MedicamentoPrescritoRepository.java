package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.MedicamentoPrescrito;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoPrescritoRepository extends JpaRepository<MedicamentoPrescrito, Long> {

    List<MedicamentoPrescrito> findByTratamientoId(Long tratamientoId);
}
