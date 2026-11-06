package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mascotas.
 *
 * Endpoints a implementar:
 *   GET    /api/mascotas
 *   GET    /api/mascotas/{id}
 *   POST   /api/mascotas              (body incluye clienteId)
 *   PUT    /api/mascotas/{id}
 *   DELETE /api/mascotas/{id}
 *   GET    /api/mascotas/{id}/historial    (atenciones, tratamientos, vacunas)
 *   GET    /api/mascotas/{id}/reservas
 *   GET    /api/mascotas/{id}/vacunas
 */
@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;
}
