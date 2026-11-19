package cl.ubiobio.petcare.repository;

import cl.ubiobio.petcare.model.entity.Pago;
import cl.ubiobio.petcare.model.enums.EstadoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByReservaId(Long reservaId);

    boolean existsByReservaIdAndEstado(Long reservaId, EstadoPago estado);

    long countByEstadoAndFechaPagoBetween(EstadoPago estado, LocalDateTime desde, LocalDateTime hasta);

    /** Suma de los montos de los pagos en un estado dentro de un periodo (0 si no hay). */
    @Query("""
            select coalesce(sum(p.monto), 0) from Pago p
            where p.estado = :estado and p.fechaPago between :desde and :hasta
            """)
    BigDecimal sumarMontos(@Param("estado") EstadoPago estado, @Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta);
}
