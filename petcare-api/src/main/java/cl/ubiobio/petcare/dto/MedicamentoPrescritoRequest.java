package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Medicamento a recetar. Se indica codigoExterno o nombre: el sistema consulta la API externa
 * y completa nombre y principio activo con los datos oficiales.
 */
public record MedicamentoPrescritoRequest(
        @Size(max = 60)
        String codigoExterno,

        @Size(max = 120)
        String nombre,

        @NotBlank(message = "La dosis es obligatoria")
        @Size(max = 60)
        String dosis,

        @NotBlank(message = "La frecuencia es obligatoria")
        @Size(max = 60)
        String frecuencia,

        @Positive(message = "La duracion debe ser mayor a 0")
        Integer duracionDias,

        @Size(max = 40)
        String viaAdministracion
) {
}
