package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.ClinicaRequest;
import cl.ubiobio.petcare.dto.ClinicaResponse;
import cl.ubiobio.petcare.dto.UbicacionClinicaResponse;
import cl.ubiobio.petcare.dto.UbicacionDto;
import cl.ubiobio.petcare.service.ClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Datos y ubicacion de la clinica (API de geolocalizacion).
 *
 *   GET    /api/clinicas
 *   GET    /api/clinicas/{id}
 *   PUT    /api/clinicas/{id}
 *   GET    /api/clinicas/{id}/ubicacion              (coordenadas + urlMapa + urlEmbed para un iframe)
 *   GET    /api/clinicas/geocodificar?direccion=      (coordenadas de cualquier direccion)
 */
@RestController
@RequestMapping("/api/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService clinicaService;

    @GetMapping
    public List<ClinicaResponse> listar() {
        return clinicaService.listar();
    }

    @GetMapping("/{id}")
    public ClinicaResponse buscarPorId(@PathVariable Long id) {
        return clinicaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ClinicaResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClinicaRequest req) {
        return clinicaService.actualizar(id, req);
    }

    @GetMapping("/{id}/ubicacion")
    public UbicacionClinicaResponse ubicacion(@PathVariable Long id) {
        return clinicaService.obtenerUbicacion(id);
    }

    @GetMapping("/geocodificar")
    public UbicacionDto geocodificar(@RequestParam String direccion) {
        return clinicaService.geocodificar(direccion);
    }
}
