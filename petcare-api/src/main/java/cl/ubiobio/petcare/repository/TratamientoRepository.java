package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Tratamiento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByAtencionId(Long atencionId);

    List<Tratamiento> findByAtencionMascotaId(Long mascotaId);
}
