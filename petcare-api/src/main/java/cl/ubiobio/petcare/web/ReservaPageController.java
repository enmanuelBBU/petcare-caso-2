package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.service.MascotaService;
import cl.ubiobio.petcare.service.NotificacionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.service.TipoServicioService;
import cl.ubiobio.petcare.service.VeterinarioService;
import cl.ubiobio.petcare.web.form.ReservaForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Paginas de reservas: listado con filtros, detalle con historial de estados y acciones de estado.
 * Las reglas de transicion viven en ReservaService; aqui solo se muestra el resultado.
 */
@Controller
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaPageController {

    private final ReservaService reservaService;
    private final NotificacionService notificacionService;
    private final VeterinarioService veterinarioService;
    private final MascotaService mascotaService;
    private final TipoServicioService tipoServicioService;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    @ModelAttribute("seccion")
    public String seccion() {
        return "reservas";
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoReserva estado,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                         @RequestParam(required = false) Long veterinarioId,
                         Model model) {
        model.addAttribute("reservas", reservaService.listar(estado, fecha, veterinarioId));
        model.addAttribute("veterinarios", veterinarioService.listar(null, false));
        model.addAttribute("estados", EstadoReserva.values());
        model.addAttribute("estado", estado);
        model.addAttribute("fecha", fecha);
        model.addAttribute("veterinarioId", veterinarioId);
        return "reservas";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long mascotaId, Model model) {
        ReservaForm form = new ReservaForm();
        form.setMascotaId(mascotaId);
        model.addAttribute("form", form);
        return vistaForm(model, "crear", null);
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") ReservaForm form, BindingResult br,
                        Model model, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return vistaForm(model, "crear", null);
        }
        try {
            ReservaResponse r = reservaService.crear(form.toRequest());
            flash.addFlashAttribute("ok", "Reserva solicitada para " + r.mascotaNombre() + " el "
                    + r.fecha().format(FECHA) + " a las " + r.hora().format(HORA) + ". Queda PENDIENTE de confirmar.");
            return "redirect:/reservas/" + r.id();
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaForm(model, "crear", null);
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("form", ReservaForm.de(reservaService.buscarPorId(id)));
        return vistaForm(model, "editar", id);
    }

    @PostMapping("/{id}")
    public String modificar(@PathVariable Long id, @Valid @ModelAttribute("form") ReservaForm form,
                            BindingResult br, Model model, RedirectAttributes flash) {
        if (br.hasErrors()) {
            return vistaForm(model, "editar", id);
        }
        try {
            ReservaResponse r = reservaService.modificar(id, form.toModificarRequest());
            flash.addFlashAttribute("ok", "Reserva modificada: " + r.fecha().format(FECHA) + " a las "
                    + r.hora().format(HORA) + ".");
            return "redirect:/reservas/" + id;
        } catch (ReglaNegocioException | ReservaDuplicadaException | RegistroDuplicadoException
                 | ApiExternaException | DataIntegrityViolationException e) {
            model.addAttribute("error", WebMensajes.de(e));
            return vistaForm(model, "editar", id);
        }
    }

    private String vistaForm(Model model, String modo, Long id) {
        model.addAttribute("modo", modo);
        model.addAttribute("id", id);
        if (id != null) {
            model.addAttribute("reserva", reservaService.buscarPorId(id));
        }
        List<MascotaResponse> mascotas = mascotaService.listar();
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("mascotasPorCliente", mascotas.stream()
                .collect(Collectors.groupingBy(MascotaResponse::clienteNombre, TreeMap::new, Collectors.toList())));
        model.addAttribute("veterinarios", veterinarioService.listar(null, false));
        model.addAttribute("servicios", tipoServicioService.listar(false));
        return "reserva-form";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("reserva", reservaService.buscarPorId(id));
        model.addAttribute("historial", reservaService.historialEstados(id));
        return "reserva-detalle";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                            RedirectAttributes flash) {
        ReservaResponse r = reservaService.confirmar(id, vacioANull(motivo));
        avisarNotificacion(r, "Reserva confirmada", flash);
        return "redirect:/reservas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                           RedirectAttributes flash) {
        ReservaResponse r = reservaService.cancelar(id, vacioANull(motivo));
        avisarNotificacion(r, "Reserva cancelada", flash);
        return "redirect:/reservas/" + id;
    }

    /** Busca la notificacion mas reciente de la reserva y cuenta en el flash si se envio o fallo. */
    private void avisarNotificacion(ReservaResponse r, String accion, RedirectAttributes flash) {
        NotificacionResponse n = notificacionService.listarPorCliente(r.clienteId()).stream()
                .filter(x -> Objects.equals(x.reservaId(), r.id()))
                .findFirst()
                .orElse(null);
        if (n == null) {
            flash.addFlashAttribute("ok", accion + ".");
        } else if (n.estado() == EstadoNotificacion.FALLIDA) {
            flash.addFlashAttribute("error", accion + ", pero la notificación falló (API externa sin conexión). "
                    + "Puede reenviarla desde Notificaciones.");
        } else {
            flash.addFlashAttribute("ok", accion + ". Notificación enviada a " + n.destinatario() + ".");
        }
    }

    static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
