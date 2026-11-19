package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Clinica;

import java.math.BigDecimal;

/** Ubicacion de la clinica para mostrarla en un mapa (urlEmbed se usa en un iframe). */
public record UbicacionClinicaResponse(
        Long clinicaId,
        String nombre,
        String direccion,
        String comuna,
        String ciudad,
        BigDecimal latitud,
        BigDecimal longitud,
        String urlMapa,
        String urlEmbed
) {
    public static UbicacionClinicaResponse de(Clinica c, UbicacionDto u) {
        return new UbicacionClinicaResponse(c.getId(), c.getNombre(), c.getDireccion(), c.getComuna(), c.getCiudad(),
                c.getLatitud(), c.getLongitud(), u.urlMapa(), u.urlEmbed());
    }
}
