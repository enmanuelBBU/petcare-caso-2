package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Historial y reenvio de notificaciones.
 *
 * Endpoints a implementar:
 *   GET    /api/notificaciones?estado=FALLIDA
 *   POST   /api/notificaciones/{id}/reenviar
 *   POST   /api/notificaciones/recordatorios   (dispara recordatorios del dia siguiente)
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;
}
