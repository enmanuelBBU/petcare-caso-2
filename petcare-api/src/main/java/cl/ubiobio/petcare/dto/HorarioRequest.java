package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/** Bloque semanal de atencion (diaSemana: 1 = lunes ... 7 = domingo). */
public record HorarioRequest(
        @NotNull(message = "El dia de la semana es obligatorio")
        @Min(value = 1, message = "diaSemana va de 1 (lunes) a 7 (domingo)")
        @Max(value = 7, message = "diaSemana va de 1 (lunes) a 7 (domingo)")
        Integer diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {
}
