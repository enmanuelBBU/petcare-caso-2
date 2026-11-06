package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.MedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Proxy hacia la API externa de medicamentos.
 *
 * Endpoints a implementar:
 *   GET    /api/medicamentos/buscar?nombre=amoxicilina
 *   GET    /api/medicamentos/{codigo}
 */
@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
}
