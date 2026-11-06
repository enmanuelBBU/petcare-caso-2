package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.AtencionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Atenciones / ficha clinica.
 *
 * Endpoints a implementar:
 *   POST   /api/atenciones
 *   GET    /api/atenciones/{id}
 *   PUT    /api/atenciones/{id}
 *   GET    /api/atenciones/{id}/tratamientos
 */
@RestController
@RequestMapping("/api/atenciones")
@RequiredArgsConstructor
public class AtencionController {

    private final AtencionService atencionService;
}
