package cl.ubiobio.petcare.exception;

/** Fallo al comunicarse con una API externa (medicamentos, mapas, notificaciones) (HTTP 502). */
public class ApiExternaException extends RuntimeException {

    public ApiExternaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
