package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Datos para crear o actualizar un servicio del catalogo. */
public record TipoServicioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80)
        String nombre,

        @Size(max = 255)
        String descripcion,

        @NotNull(message = "La duracion es obligatoria")
        @Min(value = 1, message = "La duracion debe ser de al menos 1 minuto")
        Integer duracionMinutos,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0", message = "El precio no puede ser negativo")
        BigDecimal precio,

        /** Opcional; al actualizar permite reactivar un servicio dado de baja. */
        Boolean activo
) {
}
