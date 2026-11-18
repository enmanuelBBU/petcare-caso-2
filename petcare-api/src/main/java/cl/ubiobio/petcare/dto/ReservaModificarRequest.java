package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Cambio de una reserva: nueva fecha/hora y, opcionalmente, otro veterinario, servicio o motivo.
 * Los campos opcionales en null conservan el valor actual.
 */
public record ReservaModificarRequest(
        Long veterinarioId,

        Long tipoServicioId,

        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser pasada")
        LocalDate fecha,

        @NotNull(message = "La hora es obligatoria")
        LocalTime hora,

        @Size(max = 255)
        String motivo
) {
}
