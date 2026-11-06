package cl.ubiobio.petcare.exception;

/** Ya existe una reserva activa para el mismo veterinario/mascota, dia y hora (HTTP 409). */
public class ReservaDuplicadaException extends RuntimeException {

    public ReservaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
