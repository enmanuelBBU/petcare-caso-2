package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.HistorialEstadoReserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoReservaRepository extends JpaRepository<HistorialEstadoReserva, Long> {

    List<HistorialEstadoReserva> findByReservaIdOrderByFechaCambioAsc(Long reservaId);
}
