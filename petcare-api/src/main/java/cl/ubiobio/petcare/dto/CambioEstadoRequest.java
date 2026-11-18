package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.Size;

/** Cuerpo opcional al confirmar/cancelar/atender una reserva: motivo del cambio (queda en el historial). */
public record CambioEstadoRequest(
        @Size(max = 255)
        String motivo
) {
}
