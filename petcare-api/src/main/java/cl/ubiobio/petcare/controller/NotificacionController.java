package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Historial y reenvio de notificaciones.
 *
 *   GET    /api/notificaciones?estado=FALLIDA     (filtro opcional)
 *   POST   /api/notificaciones/{id}/reenviar
 *   POST   /api/notificaciones/recordatorios      (envia recordatorios de las reservas de manana)
 *   POST   /api/notificaciones/recordatorios/vacunas?dias=7   (vacunas por vencer)
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public List<NotificacionResponse> listar(@RequestParam(required = false) EstadoNotificacion estado) {
        return notificacionService.listar(estado);
    }

    @PostMapping("/{id}/reenviar")
    public NotificacionResponse reenviar(@PathVariable Long id) {
        return notificacionService.reenviar(id);
    }

    @PostMapping("/recordatorios")
    public List<NotificacionResponse> enviarRecordatorios() {
        return notificacionService.enviarRecordatorios();
    }

    @PostMapping("/recordatorios/vacunas")
    public List<NotificacionResponse> enviarRecordatoriosVacunas(@RequestParam(defaultValue = "7") int dias) {
        return notificacionService.enviarRecordatoriosVacunas(dias);
    }
}
