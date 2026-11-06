package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.TipoServicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoServicioRepository extends JpaRepository<TipoServicio, Long> {

    List<TipoServicio> findByActivoTrue();
}
