package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.ClienteRequest;
import cl.ubiobio.petcare.dto.ClienteResponse;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.service.ClienteService;
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
 * Clientes (duenos de mascotas).
 *
 *   GET    /api/clientes
 *   GET    /api/clientes/{id}
 *   GET    /api/clientes/rut/{rut}
 *   POST   /api/clientes
 *   PUT    /api/clientes/{id}
 *   DELETE /api/clientes/{id}
 *   GET    /api/clientes/{id}/mascotas
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping("/rut/{rut}")
    public ClienteResponse buscarPorRut(@PathVariable String rut) {
        return clienteService.buscarPorRut(rut);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest req) {
        ClienteResponse creado = clienteService.crear(req);
        return ResponseEntity.created(URI.create("/api/clientes/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest req) {
        return clienteService.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/mascotas")
    public List<MascotaResponse> listarMascotas(@PathVariable Long id) {
        return clienteService.listarMascotas(id);
    }
}
