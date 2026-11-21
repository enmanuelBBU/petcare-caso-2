package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.ClinicaResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.service.ClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/** Datos de la clinica y su ubicacion en un mapa (URL entregada por la API externa de geolocalizacion). */
@Controller
@RequiredArgsConstructor
public class ClinicaPageController {

    private final ClinicaService clinicaService;

    @GetMapping("/clinica")
    public String clinica(Model model) {
        model.addAttribute("seccion", "clinica");
        List<ClinicaResponse> clinicas = clinicaService.listar();
        if (clinicas.isEmpty()) {
            model.addAttribute("mapaError", "No hay clínicas registradas.");
            return "clinica";
        }
        ClinicaResponse clinica = clinicas.get(0);
        model.addAttribute("clinica", clinica);
        try {
            model.addAttribute("ubicacion", clinicaService.obtenerUbicacion(clinica.id()));
        } catch (ApiExternaException e) {
            model.addAttribute("mapaError", "No se pudo cargar el mapa: la API externa de geolocalización no responde.");
        }
        return "clinica";
    }
}
