package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.TipoServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catalogo de servicios.
 *
 * Endpoints a implementar:
 *   GET    /api/servicios
 *   POST   /api/servicios
 *   PUT    /api/servicios/{id}
 *   DELETE /api/servicios/{id}
 */
@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class TipoServicioController {

    private final TipoServicioService tipoServicioService;
}
