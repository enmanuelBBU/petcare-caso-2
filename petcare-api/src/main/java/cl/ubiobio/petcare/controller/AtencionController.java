package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.AtencionRequest;
import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.service.AtencionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Fichas clinicas.
 *
 *   POST   /api/atenciones                   (con reservaId: marca la reserva ATENDIDA)
 *   GET    /api/atenciones/{id}
 *   PUT    /api/atenciones/{id}
 *   GET    /api/atenciones/{id}/tratamientos
 */
@RestController
@RequestMapping("/api/atenciones")
@RequiredArgsConstructor
public class AtencionController {

    private final AtencionService atencionService;

    @PostMapping
    public ResponseEntity<AtencionResponse> registrar(@Valid @RequestBody AtencionRequest req) {
        AtencionResponse creada = atencionService.registrar(req);
        return ResponseEntity.created(URI.create("/api/atenciones/" + creada.id())).body(creada);
    }

    @GetMapping("/{id}")
    public AtencionResponse buscarPorId(@PathVariable Long id) {
        return atencionService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public AtencionResponse actualizar(@PathVariable Long id, @Valid @RequestBody AtencionRequest req) {
        return atencionService.actualizar(id, req);
    }

    @GetMapping("/{id}/tratamientos")
    public List<TratamientoResponse> tratamientos(@PathVariable Long id) {
        return atencionService.tratamientosDeAtencion(id);
    }
}
