package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Horas medicas.
 *
 * Endpoints a implementar:
 *   GET    /api/reservas?estado=PENDIENTE&fecha=2026-11-20&veterinarioId=1
 *   GET    /api/reservas/{id}
 *   POST   /api/reservas
 *   PUT    /api/reservas/{id}            (modificar fecha/hora/veterinario)
 *   PATCH  /api/reservas/{id}/confirmar  (envia notificacion)
 *   PATCH  /api/reservas/{id}/cancelar   (envia notificacion)
 *   PATCH  /api/reservas/{id}/atender
 *   GET    /api/reservas/{id}/historial-estados
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
}
