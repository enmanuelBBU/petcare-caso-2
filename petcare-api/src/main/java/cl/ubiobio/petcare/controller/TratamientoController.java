package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.MedicamentoPrescritoRequest;
import cl.ubiobio.petcare.dto.TratamientoActualizarRequest;
import cl.ubiobio.petcare.dto.TratamientoRequest;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.service.TratamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Tratamientos y medicamentos prescritos.
 *
 *   POST   /api/tratamientos                       (puede incluir "medicamentos": [...])
 *   GET    /api/tratamientos/{id}
 *   PUT    /api/tratamientos/{id}
 *   POST   /api/tratamientos/{id}/medicamentos     body: { codigoExterno | nombre, dosis, frecuencia, ... }
 *   DELETE /api/tratamientos/{id}/medicamentos/{medicamentoId}
 */
@RestController
@RequestMapping("/api/tratamientos")
@RequiredArgsConstructor
public class TratamientoController {

    private final TratamientoService tratamientoService;

    @PostMapping
    public ResponseEntity<TratamientoResponse> crear(@Valid @RequestBody TratamientoRequest req) {
        TratamientoResponse creado = tratamientoService.crear(req);
        return ResponseEntity.created(URI.create("/api/tratamientos/" + creado.id())).body(creado);
    }

    @GetMapping("/{id}")
    public TratamientoResponse buscarPorId(@PathVariable Long id) {
        return tratamientoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TratamientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody TratamientoActualizarRequest req) {
        return tratamientoService.actualizar(id, req);
    }

    @PostMapping("/{id}/medicamentos")
    public ResponseEntity<TratamientoResponse> agregarMedicamento(@PathVariable Long id,
                                                                  @Valid @RequestBody MedicamentoPrescritoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tratamientoService.agregarMedicamento(id, req));
    }

    @DeleteMapping("/{id}/medicamentos/{medicamentoId}")
    public TratamientoResponse quitarMedicamento(@PathVariable Long id, @PathVariable Long medicamentoId) {
        return tratamientoService.quitarMedicamento(id, medicamentoId);
    }
}
