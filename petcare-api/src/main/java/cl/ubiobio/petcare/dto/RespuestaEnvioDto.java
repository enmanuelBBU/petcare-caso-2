package cl.ubiobio.petcare.dto;

/** Respuesta de la API externa de notificaciones: { id, canal, estado, fecha }. */
public record RespuestaEnvioDto(
        String id,
        String canal,
        String estado,
        String fecha
) {
}
