package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

/**
 * Errores de las paginas web (no de la API REST): un recurso inexistente muestra una pagina 404 propia;
 * un error de negocio en una accion de boton vuelve a la pagina de origen con un mensaje flash.
 */
@ControllerAdvice(basePackages = "cl.ubiobio.petcare.web")
public class WebExceptionAdvice {

    static final String MSG_INTEGRIDAD = "No se puede eliminar: tiene registros asociados";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView noEncontrado(ResourceNotFoundException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.setStatus(HttpStatus.NOT_FOUND);
        mv.addObject("mensaje", ex.getMessage());
        return mv;
    }

    @ExceptionHandler({ReglaNegocioException.class, ReservaDuplicadaException.class,
            RegistroDuplicadoException.class, ApiExternaException.class})
    public String negocio(RuntimeException ex, HttpServletRequest req, RedirectAttributes flash) {
        flash.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + origen(req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String integridad(HttpServletRequest req, RedirectAttributes flash) {
        flash.addFlashAttribute("error", MSG_INTEGRIDAD);
        return "redirect:" + origen(req);
    }

    /** Ruta (sin host) de la pagina de origen, para no redirigir nunca a otro sitio. */
    private static String origen(HttpServletRequest req) {
        String referer = req.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/";
        }
        try {
            URI uri = URI.create(referer.trim());
            String path = uri.getRawPath();
            if (path == null || path.isEmpty() || !path.startsWith("/")) {
                return "/";
            }
            return uri.getRawQuery() == null ? path : path + "?" + uri.getRawQuery();
        } catch (IllegalArgumentException e) {
            return "/";
        }
    }
}
