package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Veterinarios, horarios y disponibilidad.
 *
 * Endpoints a implementar:
 *   GET    /api/veterinarios
 *   GET    /api/veterinarios/{id}
 *   POST   /api/veterinarios
 *   PUT    /api/veterinarios/{id}
 *   DELETE /api/veterinarios/{id}
 *   GET    /api/veterinarios/{id}/horarios
 *   POST   /api/veterinarios/{id}/horarios
 *   DELETE /api/veterinarios/{id}/horarios/{horarioId}
 *   GET    /api/veterinarios/{id}/disponibilidad?fecha=2026-11-20
 */
@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;
}
