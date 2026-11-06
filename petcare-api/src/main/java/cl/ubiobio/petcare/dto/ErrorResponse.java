package cl.ubiobio.petcare.dto;

import java.time.LocalDateTime;
import java.util.Map;

/** Cuerpo estandar de las respuestas de error de la API. */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path,
        Map<String, String> detalles
) {
    public static ErrorResponse of(int status, String error, String mensaje, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, mensaje, path, null);
    }
}
