package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.HorarioResponse;
import cl.ubiobio.petcare.dto.VeterinarioResponse;
import cl.ubiobio.petcare.service.VeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Veterinarios con sus horarios semanales (solo lectura). */
@Controller
@RequiredArgsConstructor
public class VeterinarioPageController {

    private final VeterinarioService veterinarioService;

    @GetMapping("/veterinarios")
    public String listar(Model model) {
        List<VeterinarioResponse> veterinarios = veterinarioService.listar(null, false);
        Map<Long, List<HorarioResponse>> horarios = new HashMap<>();
        veterinarios.forEach(v -> horarios.put(v.id(), veterinarioService.listarHorarios(v.id())));
        model.addAttribute("seccion", "veterinarios");
        model.addAttribute("veterinarios", veterinarios);
        model.addAttribute("horarios", horarios);
        return "veterinarios";
    }
}
