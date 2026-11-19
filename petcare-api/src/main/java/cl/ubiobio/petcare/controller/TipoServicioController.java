package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.TipoServicioRequest;
import cl.ubiobio.petcare.dto.TipoServicioResponse;
import cl.ubiobio.petcare.service.TipoServicioService;
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
 * Catalogo de servicios.
 *
 *   GET    /api/servicios?incluirInactivos=false
 *   GET    /api/servicios/{id}
 *   POST   /api/servicios
 *   PUT    /api/servicios/{id}
 *   DELETE /api/servicios/{id}      (baja logica: activo = false)
 */
@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class TipoServicioController {

    private final TipoServicioService tipoServicioService;

    @GetMapping
    public List<TipoServicioResponse> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return tipoServicioService.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public TipoServicioResponse buscarPorId(@PathVariable Long id) {
        return tipoServicioService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TipoServicioResponse> crear(@Valid @RequestBody TipoServicioRequest req) {
        TipoServicioResponse creado = tipoServicioService.crear(req);
        return ResponseEntity.created(URI.create("/api/servicios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public TipoServicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody TipoServicioRequest req) {
        return tipoServicioService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoServicioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
