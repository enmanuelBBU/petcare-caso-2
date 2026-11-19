package cl.ubiobio.petcare.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Horas de un dia para un veterinario: las libres (se pueden reservar) y las ya ocupadas
 * por reservas activas (PENDIENTE, CONFIRMADA o ATENDIDA).
 */
public record DisponibilidadResponse(
        Long veterinarioId,
        String veterinarioNombre,
        LocalDate fecha,
        int intervaloMinutos,
        List<HorarioResponse> bloques,
        List<LocalTime> horasDisponibles,
        List<LocalTime> horasOcupadas
) {
}
