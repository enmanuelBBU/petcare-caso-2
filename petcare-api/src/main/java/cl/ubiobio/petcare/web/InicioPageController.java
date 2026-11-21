package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.client.EstadoApiClient;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/** Panel principal: agenda de hoy, reservas del mes por estado y estado de las APIs externas. */
@Controller
@RequiredArgsConstructor
public class InicioPageController {

    private final ReporteService reporteService;
    private final EstadoApiClient estadoApiClient;

    @GetMapping("/")
    public String inicio(Model model) {
        LocalDate hoy = LocalDate.now();
        model.addAttribute("seccion", "inicio");
        model.addAttribute("hoy", hoy);
        model.addAttribute("agenda", reporteService.agenda(hoy));
        model.addAttribute("resumen", reporteService.resumen(hoy.withDayOfMonth(1), hoy));
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("apisOk", estadoApiClient.disponible());
        return "inicio";
    }
}
