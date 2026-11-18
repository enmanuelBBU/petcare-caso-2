package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.MedicamentoExternoDto;
import cl.ubiobio.petcare.service.MedicamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Proxy hacia la API externa de medicamentos.
 *
 *   GET    /api/medicamentos/buscar?nombre=amoxicilina
 *   GET    /api/medicamentos/{codigo}
 */
@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @GetMapping("/buscar")
    public List<MedicamentoExternoDto> buscar(@RequestParam String nombre) {
        return medicamentoService.buscarPorNombre(nombre);
    }

    @GetMapping("/{codigo}")
    public MedicamentoExternoDto obtener(@PathVariable String codigo) {
        return medicamentoService.obtenerDetalle(codigo);
    }
}
