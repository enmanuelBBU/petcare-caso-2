package cl.ubiobio.petcare.exception;

/**
 * Se viola una regla del negocio (HTTP 400).
 * Ej: cancelar una reserva ya ATENDIDA, reservar fuera del horario del veterinario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
