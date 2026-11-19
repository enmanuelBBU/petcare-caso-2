package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Datos para registrar o actualizar una vacuna del carnet de una mascota. */
public record VacunaRequest(
        @NotNull(message = "El mascotaId es obligatorio")
        Long mascotaId,

        /** Veterinario que la aplico (opcional). */
        Long veterinarioId,

        @NotBlank(message = "El nombre de la vacuna es obligatorio")
        @Size(max = 100)
        String nombre,

        @Size(max = 40)
        String lote,

        @NotNull(message = "La fecha de aplicacion es obligatoria")
        @PastOrPresent(message = "La fecha de aplicacion no puede ser futura")
        LocalDate fechaAplicacion,

        /** Debe ser posterior a la fecha de aplicacion. */
        LocalDate fechaProximaDosis
) {
}
