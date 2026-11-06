package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Clientes (duenos de mascotas).
 *
 * Endpoints a implementar:
 *   GET    /api/clientes
 *   GET    /api/clientes/{id}
 *   GET    /api/clientes/rut/{rut}
 *   POST   /api/clientes
 *   PUT    /api/clientes/{id}
 *   DELETE /api/clientes/{id}
 *   GET    /api/clientes/{id}/mascotas
 *   GET    /api/clientes/{id}/reservas
 *   GET    /api/clientes/{id}/notificaciones
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
}
