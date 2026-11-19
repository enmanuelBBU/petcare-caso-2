package cl.ubiobio.petcare.controller;

import cl.ubiobio.petcare.dto.AgendaVeterinarioResponse;
import cl.ubiobio.petcare.dto.ResumenResponse;
import cl.ubiobio.petcare.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Reportes / panel de administracion.
 *
 *   GET    /api/reportes/resumen?desde=2026-11-01&hasta=2026-11-30   (sin fechas: mes actual)
 *   GET    /api/reportes/agenda?fecha=2026-11-20                     (sin fecha: hoy)
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public ResumenResponse resumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.resumen(desde, hasta);
    }

    @GetMapping("/agenda")
    public List<AgendaVeterinarioResponse> agenda(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return reporteService.agenda(fecha);
    }
}
