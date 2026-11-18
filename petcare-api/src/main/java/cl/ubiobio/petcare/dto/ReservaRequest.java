package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/** Solicitud de una hora medica para una mascota. tipoServicioId y clinicaId son opcionales. */
public record ReservaRequest(
        @NotNull(message = "El mascotaId es obligatorio")
        Long mascotaId,

        @NotNull(message = "El veterinarioId es obligatorio")
        Long veterinarioId,

        Long tipoServicioId,

        Long clinicaId,

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser pasada")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora,

        @Size(max = 255)
        String motivo
) {
}
