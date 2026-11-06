package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.TratamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tratamientos y medicamentos prescritos.
 *
 * Endpoints a implementar:
 *   POST   /api/tratamientos
 *   GET    /api/tratamientos/{id}
 *   PUT    /api/tratamientos/{id}
 *   POST   /api/tratamientos/{id}/medicamentos
 *   DELETE /api/tratamientos/{id}/medicamentos/{medicamentoId}
 */
@RestController
@RequestMapping("/api/tratamientos")
@RequiredArgsConstructor
public class TratamientoController {

    private final TratamientoService tratamientoService;
}
