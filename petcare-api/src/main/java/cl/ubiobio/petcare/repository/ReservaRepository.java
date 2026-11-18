package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /** Valida en la capa de servicio que no exista otra reserva activa en el mismo horario. */
    boolean existsByVeterinarioIdAndFechaAndHoraAndEstadoNot(Long veterinarioId, LocalDate fecha,
                                                             LocalTime hora, EstadoReserva estado);

    /**
     * True si el veterinario ya tiene otra reserva no cancelada en ese dia y hora.
     * excluirId permite ignorar la propia reserva al modificarla (null al crear).
     */
    @Query("""
            select count(r) > 0 from Reserva r
            where r.veterinario.id = :veterinarioId and r.fecha = :fecha and r.hora = :hora
              and r.estado <> :cancelada and (:excluirId is null or r.id <> :excluirId)
            """)
    boolean existeChoqueVeterinario(@Param("veterinarioId") Long veterinarioId, @Param("fecha") LocalDate fecha,
                                    @Param("hora") LocalTime hora, @Param("cancelada") EstadoReserva cancelada,
                                    @Param("excluirId") Long excluirId);

    /** True si la mascota ya tiene otra reserva no cancelada en ese dia y hora. */
    @Query("""
            select count(r) > 0 from Reserva r
            where r.mascota.id = :mascotaId and r.fecha = :fecha and r.hora = :hora
              and r.estado <> :cancelada and (:excluirId is null or r.id <> :excluirId)
            """)
    boolean existeChoqueMascota(@Param("mascotaId") Long mascotaId, @Param("fecha") LocalDate fecha,
                                @Param("hora") LocalTime hora, @Param("cancelada") EstadoReserva cancelada,
                                @Param("excluirId") Long excluirId);

    /** Listado con filtros opcionales (un parametro null no filtra). */
    @Query("""
            select r from Reserva r
            where (:estado is null or r.estado = :estado)
              and (:fecha is null or r.fecha = :fecha)
              and (:veterinarioId is null or r.veterinario.id = :veterinarioId)
            order by r.fecha, r.hora
            """)
    List<Reserva> buscar(@Param("estado") EstadoReserva estado, @Param("fecha") LocalDate fecha,
                         @Param("veterinarioId") Long veterinarioId);

    List<Reserva> findByVeterinarioIdAndFechaAndEstadoNot(Long veterinarioId, LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByMascotaIdOrderByFechaDescHoraDesc(Long mascotaId);

    List<Reserva> findByMascotaClienteIdOrderByFechaDescHoraDesc(Long clienteId);

    List<Reserva> findByEstado(EstadoReserva estado);

    List<Reserva> findByFechaAndEstado(LocalDate fecha, EstadoReserva estado);
}
