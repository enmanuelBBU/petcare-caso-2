package cl.ubiobio.petcare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/** Nuevo tratamiento para una atencion; puede incluir de inmediato los medicamentos recetados. */
public record TratamientoRequest(
        @NotNull(message = "El atencionId es obligatorio")
        Long atencionId,

        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 500)
        String descripcion,

        @Size(max = 1000)
        String indicaciones,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        LocalDate fechaFin,

        List<@Valid MedicamentoPrescritoRequest> medicamentos
) {
}
