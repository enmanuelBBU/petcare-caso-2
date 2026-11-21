package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.service.AtencionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.service.TratamientoService;
import cl.ubiobio.petcare.web.form.AtencionForm;
import cl.ubiobio.petcare.web.form.TratamientoForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Registro de la atencion de una reserva y del tratamiento (con medicamentos de la API externa). */
@Controller
@RequiredArgsConstructor
public class AtencionPageController {

    private final AtencionService atencionService;
    private final TratamientoService tratamientoService;
    private final ReservaService reservaService;

    @ModelAttribute("seccion")
    public String seccion() {
        return "reservas";
    }

    // ------------------------------------------------------------------ atencion

    @GetMapping("/atenciones/nueva")
    public String nuevaAtencion(@RequestParam Long reservaId, Model model) {
        AtencionForm form = new AtencionForm();
        form.setReservaId(reservaId);
        model.addAttribute("form", form);
        return vistaAtencion(model, reservaId);
    }

    @PostMapping("/atenciones")
    public String registrar(@Valid @ModelAttribute("form") AtencionForm form, BindingResult br,
                            Model model, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return vistaAtencion(model, form.getReservaId());
        }
        try {
            AtencionResponse a = atencionService.registrar(form.toRequest());
            flash.addFlashAttribute("ok", "Atención registrada. La reserva quedó ATENDIDA.");
            return "redirect:/tratamientos/nuevo?atencionId=" + a.id();
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaAtencion(model, form.getReservaId());
        }
    }

    private String vistaAtencion(Model model, Long reservaId) {
        if (reservaId != null) {
            model.addAttribute("reserva", reservaService.buscarPorId(reservaId));
        }
        return "atencion-form";
    }

    // ------------------------------------------------------------------ tratamiento

    @GetMapping("/tratamientos/nuevo")
    public String nuevoTratamiento(@RequestParam Long atencionId, Model model) {
        TratamientoForm form = new TratamientoForm();
        form.setAtencionId(atencionId);
        model.addAttribute("form", form);
        return vistaTratamiento(model, atencionId);
    }

    @PostMapping("/tratamientos")
    public String crearTratamiento(@Valid @ModelAttribute("form") TratamientoForm form, BindingResult br,
                                   Model model, RedirectAttributes flash) {
        form.validarMedicamentos(br);
        if (br.hasErrors()) {
            return vistaTratamiento(model, form.getAtencionId());
        }
        try {
            tratamientoService.crear(form.toRequest());
            AtencionResponse a = atencionService.buscarPorId(form.getAtencionId());
            flash.addFlashAttribute("ok", "Tratamiento registrado en el historial de " + a.mascotaNombre() + ".");
            return "redirect:/mascotas/" + a.mascotaId();
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaTratamiento(model, form.getAtencionId());
        }
    }

    private String vistaTratamiento(Model model, Long atencionId) {
        if (atencionId != null) {
            model.addAttribute("atencion", atencionService.buscarPorId(atencionId));
        }
        return "tratamiento-form";
    }
}
