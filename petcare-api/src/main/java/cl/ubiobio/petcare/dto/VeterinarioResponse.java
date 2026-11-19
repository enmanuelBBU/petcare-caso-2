package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Veterinario;

/** Veterinario devuelto por la API. */
public record VeterinarioResponse(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String especialidad,
        Long clinicaId,
        String clinicaNombre,
        Boolean activo
) {
    public static VeterinarioResponse de(Veterinario v) {
        return new VeterinarioResponse(v.getId(), v.getRut(), v.getNombre(), v.getApellido(), v.getEmail(),
                v.getTelefono(), v.getEspecialidad(),
                v.getClinica() != null ? v.getClinica().getId() : null,
                v.getClinica() != null ? v.getClinica().getNombre() : null,
                v.getActivo());
    }
}
