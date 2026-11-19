package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.VacunaRequest;
import cl.ubiobio.petcare.dto.VacunaResponse;
import cl.ubiobio.petcare.service.VacunaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Carnet de vacunacion (el carnet de una mascota esta en GET /api/mascotas/{id}/vacunas).
 *
 *   POST   /api/vacunas
 *   GET    /api/vacunas/{id}
 *   PUT    /api/vacunas/{id}
 *   DELETE /api/vacunas/{id}
 *   GET    /api/vacunas/proximas?dias=30     (proximas dosis por vencer)
 */
@RestController
@RequestMapping("/api/vacunas")
@RequiredArgsConstructor
public class VacunaController {

    private final VacunaService vacunaService;

    @PostMapping
    public ResponseEntity<VacunaResponse> registrar(@Valid @RequestBody VacunaRequest req) {
        VacunaResponse creada = vacunaService.registrar(req);
        return ResponseEntity.created(URI.create("/api/vacunas/" + creada.id())).body(creada);
    }

    @GetMapping("/proximas")
    public List<VacunaResponse> proximas(@RequestParam(defaultValue = "30") int dias) {
        return vacunaService.proximasDosis(dias);
    }

    @GetMapping("/{id}")
    public VacunaResponse buscarPorId(@PathVariable Long id) {
        return vacunaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public VacunaResponse actualizar(@PathVariable Long id, @Valid @RequestBody VacunaRequest req) {
        return vacunaService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vacunaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
