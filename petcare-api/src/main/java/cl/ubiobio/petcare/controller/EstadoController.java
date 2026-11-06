package cl.ubiobio.petcare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/** Endpoint simple para verificar que la API esta levantada. */
@RestController
@RequestMapping("/api/estado")
public class EstadoController {

    @GetMapping
    public Map<String, Object> estado() {
        return Map.of(
                "servicio", "petcare-api",
                "estado", "OK",
                "hora", LocalDateTime.now().toString()
        );
    }
}
