package cl.ubiobio.petcare.web;

import org.springframework.dao.DataIntegrityViolationException;

/** Texto que se muestra al usuario cuando un formulario falla por una regla del backend. */
final class WebMensajes {

    private WebMensajes() {
    }

    static String de(RuntimeException e) {
        return e instanceof DataIntegrityViolationException
                ? "El registro entra en conflicto con datos existentes"
                : e.getMessage();
    }
}
