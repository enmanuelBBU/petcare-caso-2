package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.dto.MascotaRequest;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.dto.VacunaResponse;
import cl.ubiobio.petcare.service.AtencionService;
import cl.ubiobio.petcare.service.MascotaService;
import cl.ubiobio.petcare.service.ReservaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Mascotas.
 *
 *   GET    /api/mascotas
 *   GET    /api/mascotas/{id}
 *   POST   /api/mascotas              (body incluye clienteId)
 *   PUT    /api/mascotas/{id}
 *   DELETE /api/mascotas/{id}
 *   GET    /api/mascotas/{id}/reservas
 *   GET    /api/mascotas/{id}/historial    (atenciones, tratamientos y medicamentos)
 *   GET    /api/mascotas/{id}/vacunas      (carnet de vacunacion)
 */
@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;
    private final ReservaService reservaService;
    private final AtencionService atencionService;
    private final VacunaService vacunaService;

    @GetMapping
    public List<MascotaResponse> listar() {
        return mascotaService.listar();
    }

    @GetMapping("/{id}")
    public MascotaResponse buscarPorId(@PathVariable Long id) {
        return mascotaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<MascotaResponse> crear(@Valid @RequestBody MascotaRequest req) {
        MascotaResponse creada = mascotaService.crear(req);
        return ResponseEntity.created(URI.create("/api/mascotas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public MascotaResponse actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequest req) {
        return mascotaService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reservas")
    public List<ReservaResponse> listarReservas(@PathVariable Long id) {
        return reservaService.listarPorMascota(id);
    }

    /** Historial clinico: atenciones con diagnostico, tratamientos y medicamentos. */
    @GetMapping("/{id}/historial")
    public List<AtencionResponse> historial(@PathVariable Long id) {
        return atencionService.historialPorMascota(id);
    }

    /** Carnet de vacunacion de la mascota. */
    @GetMapping("/{id}/vacunas")
    public List<VacunaResponse> vacunas(@PathVariable Long id) {
        return vacunaService.listarPorMascota(id);
    }
}
