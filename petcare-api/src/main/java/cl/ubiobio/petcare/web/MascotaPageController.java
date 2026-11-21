package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.model.enums.Sexo;
import cl.ubiobio.petcare.service.AtencionService;
import cl.ubiobio.petcare.service.ClienteService;
import cl.ubiobio.petcare.service.MascotaService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.web.form.MascotaForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Paginas de mascotas: listado, ficha con historial clinico y formulario crear/editar. */
@Controller
@RequestMapping("/mascotas")
@RequiredArgsConstructor
public class MascotaPageController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;
    private final AtencionService atencionService;
    private final ReservaService reservaService;

    @ModelAttribute("seccion")
    public String seccion() {
        return "mascotas";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mascotas", mascotaService.listar());
        return "mascotas";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long clienteId, Model model) {
        MascotaForm form = new MascotaForm();
        form.setClienteId(clienteId);
        model.addAttribute("form", form);
        return vistaForm(model, "crear", null);
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") MascotaForm form, BindingResult br,
                        Model model, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return vistaForm(model, "crear", null);
        }
        try {
            MascotaResponse m = mascotaService.crear(form.toRequest());
            flash.addFlashAttribute("ok", "Mascota " + m.nombre() + " registrada.");
            return "redirect:/clientes/" + m.clienteId();
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaForm(model, "crear", null);
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.buscarPorId(id));
        model.addAttribute("historial", atencionService.historialPorMascota(id));
        model.addAttribute("reservas", reservaService.listarPorMascota(id));
        return "mascota-detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", MascotaForm.de(mascotaService.buscarPorId(id)));
        return vistaForm(model, "editar", id);
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") MascotaForm form,
                             BindingResult br, Model model, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return vistaForm(model, "editar", id);
        }
        try {
            mascotaService.actualizar(id, form.toRequest());
            flash.addFlashAttribute("ok", "Datos de la mascota actualizados.");
            return "redirect:/mascotas/" + id;
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaForm(model, "editar", id);
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        MascotaResponse m = mascotaService.buscarPorId(id);
        mascotaService.eliminar(id);
        flash.addFlashAttribute("ok", "Mascota " + m.nombre() + " eliminada.");
        return "redirect:/clientes/" + m.clienteId();
    }

    private String vistaForm(Model model, String modo, Long id) {
        model.addAttribute("modo", modo);
        model.addAttribute("id", id);
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("sexos", Sexo.values());
        return "mascota-form";
    }
}
