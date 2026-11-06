package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.VacunaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Carnet de vacunacion.
 *
 * Endpoints a implementar:
 *   POST   /api/vacunas
 *   PUT    /api/vacunas/{id}
 *   DELETE /api/vacunas/{id}
 *   GET    /api/vacunas/proximas?dias=30
 */
@RestController
@RequestMapping("/api/vacunas")
@RequiredArgsConstructor
public class VacunaController {

    private final VacunaService vacunaService;
}
