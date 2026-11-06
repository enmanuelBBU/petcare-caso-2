package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** Valida en la capa de servicio que no exista otra reserva activa en el mismo horario. */
    boolean existsByVeterinarioIdAndFechaAndHoraAndEstadoNot(Long veterinarioId, LocalDate fecha,
                                                             LocalTime hora, EstadoReserva estado);

    List<Reserva> findByVeterinarioIdAndFechaAndEstadoNot(Long veterinarioId, LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByMascotaIdOrderByFechaDescHoraDesc(Long mascotaId);

    List<Reserva> findByMascotaClienteIdOrderByFechaDescHoraDesc(Long clienteId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByFechaAndEstado(LocalDate fecha, EstadoReserva estado);
}
