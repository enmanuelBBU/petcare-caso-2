package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.HistorialEstadoReserva;
import cl.ubiobio.petcare.model.enums.EstadoReserva;

import java.time.LocalDateTime;

/** Un cambio de estado de una reserva (auditoria). */
public record HistorialEstadoResponse(
        Long id,
        EstadoReserva estadoAnterior,
        EstadoReserva estadoNuevo,
        String motivo,
        LocalDateTime fechaCambio
) {
    public static HistorialEstadoResponse de(HistorialEstadoReserva h) {
        return new HistorialEstadoResponse(h.getId(), h.getEstadoAnterior(), h.getEstadoNuevo(),
                h.getMotivo(), h.getFechaCambio());
    }
}
