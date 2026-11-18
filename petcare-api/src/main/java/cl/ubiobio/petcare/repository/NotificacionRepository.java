package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Notificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByClienteIdOrderByCreadoEnDesc(Long clienteId);

    List<Notificacion> findByReservaId(Long reservaId);

    List<Notificacion> findAllByOrderByCreadoEnDesc();

    List<Notificacion> findByEstadoOrderByCreadoEnDesc(EstadoNotificacion estado);
}
