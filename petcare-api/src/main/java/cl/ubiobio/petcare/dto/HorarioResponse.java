package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.HorarioVeterinario;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

/** Bloque semanal de atencion de un veterinario. */
public record HorarioResponse(
        Long id,
        Integer diaSemana,
        String dia,
        LocalTime horaInicio,
        LocalTime horaFin
) {
    private static final Locale ES = Locale.forLanguageTag("es-CL");

    public static HorarioResponse de(HorarioVeterinario h) {
        String dia = DayOfWeek.of(h.getDiaSemana()).getDisplayName(TextStyle.FULL, ES);
        return new HorarioResponse(h.getId(), h.getDiaSemana(), dia, h.getHoraInicio(), h.getHoraFin());
    }
}
