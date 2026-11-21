package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Notificaciones enviadas a los clientes (API externa de notificaciones): listado, reenvio y recordatorios. */
@Controller
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionPageController {

    private final NotificacionService notificacionService;

    @ModelAttribute("seccion")
    public String seccion() {
        return "notificaciones";
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoNotificacion estado, Model model) {
        model.addAttribute("notificaciones", notificacionService.listar(estado));
        model.addAttribute("estados", EstadoNotificacion.values());
        model.addAttribute("estado", estado);
        return "notificaciones";
    }

    @PostMapping("/{id}/reenviar")
    public String reenviar(@PathVariable Long id, RedirectAttributes flash) {
        NotificacionResponse n = notificacionService.reenviar(id);
        if (n.estado() == EstadoNotificacion.ENVIADA) {
            flash.addFlashAttribute("ok", "Notificación reenviada a " + n.destinatario() + ".");
        } else {
            flash.addFlashAttribute("error", "La notificación volvió a fallar: la API externa no responde.");
        }
        return "redirect:/notificaciones";
    }

    @PostMapping("/recordatorios")
    public String recordatorios(RedirectAttributes flash) {
        int n = notificacionService.enviarRecordatorios().size();
        flash.addFlashAttribute("ok", "Se enviaron " + n + " recordatorios de reservas de mañana.");
        return "redirect:/notificaciones";
    }
}
