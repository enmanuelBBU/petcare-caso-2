package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.CambioEstadoRequest;
import cl.ubiobio.petcare.dto.HistorialEstadoResponse;
import cl.ubiobio.petcare.dto.ReservaModificarRequest;
import cl.ubiobio.petcare.dto.ReservaRequest;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
 * Horas medicas.
 *
 *   GET    /api/reservas?estado=PENDIENTE&fecha=2026-11-20&veterinarioId=1   (filtros opcionales)
 *   GET    /api/reservas/{id}
 *   POST   /api/reservas
 *   PUT    /api/reservas/{id}            (modificar fecha/hora/veterinario)
 *   PATCH  /api/reservas/{id}/confirmar  body opcional: { "motivo": "..." }
 *   PATCH  /api/reservas/{id}/cancelar   body opcional: { "motivo": "..." }
 *   PATCH  /api/reservas/{id}/atender
 *   GET    /api/reservas/{id}/historial-estados
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public List<ReservaResponse> listar(
            @RequestParam(required = false) EstadoReserva estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long veterinarioId) {
        return reservaService.listar(estado, fecha, veterinarioId);
    }

    @GetMapping("/{id}")
    public ReservaResponse buscarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest req) {
        ReservaResponse creada = reservaService.crear(req);
        return ResponseEntity.created(URI.create("/api/reservas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public ReservaResponse modificar(@PathVariable Long id, @Valid @RequestBody ReservaModificarRequest req) {
        return reservaService.modificar(id, req);
    }

    @PatchMapping("/{id}/confirmar")
    public ReservaResponse confirmar(@PathVariable Long id,
                                     @Valid @RequestBody(required = false) CambioEstadoRequest req) {
        return reservaService.confirmar(id, motivo(req));
    }

    @PatchMapping("/{id}/cancelar")
    public ReservaResponse cancelar(@PathVariable Long id,
                                    @Valid @RequestBody(required = false) CambioEstadoRequest req) {
        return reservaService.cancelar(id, motivo(req));
    }

    @PatchMapping("/{id}/atender")
    public ReservaResponse atender(@PathVariable Long id,
                                   @Valid @RequestBody(required = false) CambioEstadoRequest req) {
        return reservaService.marcarAtendida(id, motivo(req));
    }

    @GetMapping("/{id}/historial-estados")
    public List<HistorialEstadoResponse> historialEstados(@PathVariable Long id) {
        return reservaService.historialEstados(id);
    }

    private static String motivo(CambioEstadoRequest req) {
        return req != null && req.motivo() != null && !req.motivo().isBlank() ? req.motivo() : null;
    }
}
