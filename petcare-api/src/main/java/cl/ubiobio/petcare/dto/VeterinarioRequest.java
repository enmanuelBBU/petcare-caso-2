package cl.ubiobio.petcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear o actualizar un veterinario. */
public record VeterinarioRequest(
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

        @Size(max = 20)
        String telefono,

        @Size(max = 80)
        String especialidad,

        /** Clinica donde atiende (opcional). */
        Long clinicaId,

        /** Solo se usa al actualizar: permite reactivar un veterinario dado de baja. */
        Boolean activo
) {
}
