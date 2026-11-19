package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.PagoRequest;
import cl.ubiobio.petcare.dto.PagoResponse;
import cl.ubiobio.petcare.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Pagos.
 *
 *   POST   /api/pagos                        { reservaId, metodo, monto? }
 *   GET    /api/pagos/reserva/{reservaId}
 *   PATCH  /api/pagos/{id}/anular
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<PagoResponse> registrar(@Valid @RequestBody PagoRequest req) {
        PagoResponse creado = pagoService.registrar(req);
        return ResponseEntity.created(URI.create("/api/pagos/reserva/" + creado.reservaId())).body(creado);
    }

    @GetMapping("/reserva/{reservaId}")
    public List<PagoResponse> listarPorReserva(@PathVariable Long reservaId) {
        return pagoService.listarPorReserva(reservaId);
    }

    @PatchMapping("/{id}/anular")
    public PagoResponse anular(@PathVariable Long id) {
        return pagoService.anular(id);
    }
}
