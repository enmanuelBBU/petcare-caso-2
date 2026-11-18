package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear o actualizar un cliente. */
public record ClienteRequest(
        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^\\d{7,8}-[\\dkK]$", message = "El RUT debe tener el formato 12345678-9")
        String rut,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80)
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 80)
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no es valido")
        @Size(max = 120)
        String email,

        @NotBlank(message = "El telefono es obligatorio")
        @Size(max = 20)
        String telefono,

        @Size(max = 200)
        String direccion
) {
}
