package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Cambios a un tratamiento existente (los medicamentos se gestionan con sus propios endpoints). */
public record TratamientoActualizarRequest(
        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 500)
        String descripcion,

        @Size(max = 1000)
        String indicaciones,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin
) {
}
