package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.ClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Datos y ubicacion de la clinica (API de geolocalizacion).
 *
 * Endpoints a implementar:
 *   GET    /api/clinicas
 *   GET    /api/clinicas/{id}
 *   PUT    /api/clinicas/{id}
 *   GET    /api/clinicas/{id}/ubicacion
 */
@RestController
@RequestMapping("/api/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService clinicaService;
}
