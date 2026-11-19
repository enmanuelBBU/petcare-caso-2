package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Datos para actualizar una clinica.
 * Si se cambia la direccion sin indicar latitud/longitud, las coordenadas se recalculan
 * con la API de geolocalizacion la proxima vez que se consulte la ubicacion.
 */
public record ClinicaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 200)
        String direccion,

        @Size(max = 80)
        String comuna,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80)
        String ciudad,

        @Size(max = 20)
        String telefono,

        @Email(message = "El email no es valido")
        @Size(max = 120)
        String email,

        @DecimalMin(value = "-90", message = "Latitud fuera de rango")
        @DecimalMax(value = "90", message = "Latitud fuera de rango")
        BigDecimal latitud,

        @DecimalMin(value = "-180", message = "Longitud fuera de rango")
        @DecimalMax(value = "180", message = "Longitud fuera de rango")
        BigDecimal longitud,

        LocalTime horaApertura,

        LocalTime horaCierre
) {
}
