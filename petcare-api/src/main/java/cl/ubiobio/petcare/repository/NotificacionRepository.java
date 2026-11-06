package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Notificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByClienteIdOrderByCreadoEnDesc(Long clienteId);

    List<Notificacion> findByReservaId(Long reservaId);
}
