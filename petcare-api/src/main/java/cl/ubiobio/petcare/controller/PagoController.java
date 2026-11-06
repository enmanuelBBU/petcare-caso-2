package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pagos.
 *
 * Endpoints a implementar:
 *   POST   /api/pagos
 *   GET    /api/pagos/reserva/{reservaId}
 *   PATCH  /api/pagos/{id}/anular
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
}
