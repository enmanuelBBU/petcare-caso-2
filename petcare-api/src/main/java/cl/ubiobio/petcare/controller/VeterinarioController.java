package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.DisponibilidadResponse;
import cl.ubiobio.petcare.dto.HorarioRequest;
import cl.ubiobio.petcare.dto.HorarioResponse;
import cl.ubiobio.petcare.dto.VeterinarioRequest;
import cl.ubiobio.petcare.dto.VeterinarioResponse;
import cl.ubiobio.petcare.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Veterinarios, horarios y disponibilidad.
 *
 *   GET    /api/veterinarios?especialidad=cirugia&incluirInactivos=false
 *   GET    /api/veterinarios/{id}
 *   POST   /api/veterinarios
 *   PUT    /api/veterinarios/{id}
 *   DELETE /api/veterinarios/{id}          (baja logica si ya tiene reservas o atenciones)
 *   GET    /api/veterinarios/{id}/horarios
 *   POST   /api/veterinarios/{id}/horarios
 *   DELETE /api/veterinarios/{id}/horarios/{horarioId}
 *   GET    /api/veterinarios/{id}/disponibilidad?fecha=2026-11-20
 */
@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    public List<VeterinarioResponse> listar(@RequestParam(required = false) String especialidad,
                                            @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return veterinarioService.listar(especialidad, incluirInactivos);
    }

    @GetMapping("/{id}")
    public VeterinarioResponse buscarPorId(@PathVariable Long id) {
        return veterinarioService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponse> crear(@Valid @RequestBody VeterinarioRequest req) {
        VeterinarioResponse creado = veterinarioService.crear(req);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public VeterinarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody VeterinarioRequest req) {
        return veterinarioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/horarios")
    public List<HorarioResponse> listarHorarios(@PathVariable Long id) {
        return veterinarioService.listarHorarios(id);
    }

    @PostMapping("/{id}/horarios")
    public ResponseEntity<HorarioResponse> agregarHorario(@PathVariable Long id, @Valid @RequestBody HorarioRequest req) {
        HorarioResponse creado = veterinarioService.agregarHorario(id, req);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + id + "/horarios/" + creado.id())).body(creado);
    }

    @DeleteMapping("/{id}/horarios/{horarioId}")
    public ResponseEntity<Void> quitarHorario(@PathVariable Long id, @PathVariable Long horarioId) {
        veterinarioService.quitarHorario(id, horarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidad")
    public DisponibilidadResponse disponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return veterinarioService.calcularDisponibilidad(id, fecha);
    }
}
