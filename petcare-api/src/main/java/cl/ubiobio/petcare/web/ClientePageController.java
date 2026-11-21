package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.ClienteResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.service.ClienteService;
import cl.ubiobio.petcare.service.NotificacionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.web.form.ClienteForm;
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

import java.util.List;

/** Paginas de clientes: listado, busqueda por RUT, ficha y formulario crear/editar. */
@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientePageController {

    private final ClienteService clienteService;
    private final ReservaService reservaService;
    private final NotificacionService notificacionService;

    @ModelAttribute("seccion")
    public String seccion() {
        return "clientes";
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String rut, Model model) {
        List<ClienteResponse> clientes;
        if (rut == null || rut.isBlank()) {
            clientes = clienteService.listar();
        } else {
            try {
                clientes = List.of(clienteService.buscarPorRut(rut.trim()));
            } catch (ResourceNotFoundException e) {
                clientes = List.of();
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("clientes", clientes);
        model.addAttribute("rut", rut);
        return "clientes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new ClienteForm());
        model.addAttribute("modo", "crear");
        return "cliente-form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") ClienteForm form, BindingResult br,
                        Model model, RedirectAttributes flash) {
        model.addAttribute("modo", "crear");
        if (br.hasErrors()) {
            return "cliente-form";
        }
        try {
            ClienteResponse c = clienteService.crear(form.toRequest());
            flash.addFlashAttribute("ok", "Cliente " + c.nombre() + " " + c.apellido() + " registrado.");
            return "redirect:/clientes/" + c.id();
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return "cliente-form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        model.addAttribute("mascotas", clienteService.listarMascotas(id));
        model.addAttribute("reservas", reservaService.listarPorCliente(id));
        model.addAttribute("notificaciones", notificacionService.listarPorCliente(id));
        return "cliente-detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", ClienteForm.de(clienteService.buscarPorId(id)));
        model.addAttribute("modo", "editar");
        model.addAttribute("id", id);
        return "cliente-form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ClienteForm form,
                             BindingResult br, Model model, RedirectAttributes flash) {
        model.addAttribute("modo", "editar");
        model.addAttribute("id", id);
        if (br.hasErrors()) {
            return "cliente-form";
        }
        try {
            clienteService.actualizar(id, form.toRequest());
            flash.addFlashAttribute("ok", "Datos del cliente actualizados.");
            return "redirect:/clientes/" + id;
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return "cliente-form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        clienteService.eliminar(id);
        flash.addFlashAttribute("ok", "Cliente eliminado.");
        return "redirect:/clientes";
    }
}
