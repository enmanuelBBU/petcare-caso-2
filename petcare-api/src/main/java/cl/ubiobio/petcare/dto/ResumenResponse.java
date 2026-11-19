package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.enums.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/** Indicadores del periodo [desde, hasta] para el panel de administracion. */
public record ResumenResponse(
        LocalDate desde,
        LocalDate hasta,
        long totalReservas,
        Map<EstadoReserva, Long> reservasPorEstado,
        long atenciones,
        long pagos,
        BigDecimal ingresos
) {
}
