package cl.ubiobio.petcare.dto;

import java.time.LocalDate;
import java.util.List;

/** Reservas activas de un veterinario en un dia, ordenadas por hora. */
public record AgendaVeterinarioResponse(
        Long veterinarioId,
        String veterinarioNombre,
        LocalDate fecha,
        int totalReservas,
        List<ReservaResponse> reservas
) {
}
