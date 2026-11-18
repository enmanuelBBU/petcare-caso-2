package cl.ubiobio.petcare.exception;

/** Ya existe un registro con un dato que debe ser unico (ej. RUT o email de cliente) (HTTP 409). */
public class RegistroDuplicadoException extends RuntimeException {

    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
