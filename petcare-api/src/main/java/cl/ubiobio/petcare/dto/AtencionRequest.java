package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Registro de una atencion (ficha clinica).
 * Con reservaId se toman mascota y veterinario de la reserva y esta queda ATENDIDA;
 * sin reserva (atencion espontanea) mascotaId y veterinarioId son obligatorios.
 */
public record AtencionRequest(
        Long reservaId,

        Long mascotaId,

        Long veterinarioId,

        @Size(max = 255)
        String motivoConsulta,

        @Size(max = 1000)
        String anamnesis,

        @NotBlank(message = "El diagnostico es obligatorio")
        @Size(max = 1000)
        String diagnostico,

        @DecimalMin(value = "0.01", message = "El peso debe ser mayor a 0")
        BigDecimal pesoRegistrado,

        @DecimalMin(value = "30.0", message = "Temperatura fuera de rango")
        @DecimalMax(value = "45.0", message = "Temperatura fuera de rango")
        BigDecimal temperatura,

        @Size(max = 1000)
        String observaciones
) {
}
