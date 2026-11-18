package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Mascota;
import cl.ubiobio.petcare.model.enums.Sexo;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Mascota devuelta por la API (incluye el id y nombre de su dueno). */
public record MascotaResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        String nombre,
        String especie,
        String raza,
        Sexo sexo,
        LocalDate fechaNacimiento,
        BigDecimal peso,
        String color,
        String microchip,
        Boolean activo
) {
    public static MascotaResponse de(Mascota m) {
        return new MascotaResponse(m.getId(), m.getCliente().getId(),
                m.getCliente().getNombre() + " " + m.getCliente().getApellido(),
                m.getNombre(), m.getEspecie(), m.getRaza(), m.getSexo(), m.getFechaNacimiento(),
                m.getPeso(), m.getColor(), m.getMicrochip(), m.getActivo());
    }
}
