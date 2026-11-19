package cl.ubiobio.petcare.dto;

import java.math.BigDecimal;

/** Respuesta de la API externa de geolocalizacion (GET /api/geo/ubicacion y /api/geo/geocodificar). */
public record UbicacionDto(
        String direccion,
        BigDecimal latitud,
        BigDecimal longitud,
        String urlMapa,
        String urlEmbed
) {
}
