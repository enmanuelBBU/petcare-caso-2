package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reportes / panel de administracion.
 *
 * Endpoints a implementar:
 *   GET    /api/reportes/resumen?desde=2026-11-01&hasta=2026-11-30
 *   GET    /api/reportes/agenda?fecha=2026-11-20
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
}
