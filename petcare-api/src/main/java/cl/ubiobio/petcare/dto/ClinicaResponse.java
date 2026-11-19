package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Clinica;

import java.math.BigDecimal;
import java.time.LocalTime;

/** Clinica devuelta por la API. */
public record ClinicaResponse(
        Long id,
        String nombre,
        String direccion,
        String comuna,
        String ciudad,
        String telefono,
        String email,
        BigDecimal latitud,
        BigDecimal longitud,
        LocalTime horaApertura,
        LocalTime horaCierre
) {
    public static ClinicaResponse de(Clinica c) {
        return new ClinicaResponse(c.getId(), c.getNombre(), c.getDireccion(), c.getComuna(), c.getCiudad(),
                c.getTelefono(), c.getEmail(), c.getLatitud(), c.getLongitud(), c.getHoraApertura(), c.getHoraCierre());
    }
}
