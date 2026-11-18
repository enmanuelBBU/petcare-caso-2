package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.enums.Sexo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos para crear o actualizar una mascota. */
public record MascotaRequest(
        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80)
        String nombre,

        @NotBlank(message = "La especie es obligatoria")
        @Size(max = 40)
        String especie,

        @Size(max = 80)
        String raza,

        Sexo sexo,

        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento,

        @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
        BigDecimal peso,

        @Size(max = 40)
        String color,

        @Size(max = 30)
        String microchip
) {
}
