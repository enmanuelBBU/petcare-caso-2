package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Cliente;

import java.time.LocalDateTime;

/** Cliente devuelto por la API. */
public record ClienteResponse(
        Long id,
        String rut,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String direccion,
        LocalDateTime creadoEn
) {
    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getRut(), c.getNombre(), c.getApellido(), c.getEmail(),
                c.getTelefono(), c.getDireccion(), c.getCreadoEn());
    }
}
