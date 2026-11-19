package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.client.NotificacionApiClient;
import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.dto.RespuestaEnvioDto;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Cliente;
import cl.ubiobio.petcare.model.entity.Notificacion;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.entity.Vacuna;
import cl.ubiobio.petcare.model.enums.CanalNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.model.enums.TipoNotificacion;
import cl.ubiobio.petcare.repository.ClienteRepository;
import cl.ubiobio.petcare.repository.NotificacionRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.VacunaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Envio y registro de notificaciones a clientes por la API externa (Node).
 * Si la API externa falla, la notificacion queda FALLIDA y se puede reenviar;
 * el cambio en la reserva NO se revierte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificacionService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final NotificacionRepository notificacionRepository;
    private final NotificacionApiClient notificacionApiClient;
    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;
    private final VacunaRepository vacunaRepository;

    // ------------------------------------------------------------------ eventos de reservas

    @Transactional
    public Notificacion notificarConfirmacion(Reserva r) {
        return notificar(r, TipoNotificacion.CONFIRMACION, "Reserva confirmada - PetCare",
                "Su hora para %s el %s a las %s con %s fue CONFIRMADA.");
    }

    @Transactional
    public Notificacion notificarCancelacion(Reserva r) {
        return notificar(r, TipoNotificacion.CANCELACION, "Reserva cancelada - PetCare",
                "Su hora para %s el %s a las %s con %s fue CANCELADA.");
    }

    @Transactional
    public Notificacion notificarModificacion(Reserva r) {
        return notificar(r, TipoNotificacion.MODIFICACION, "Reserva modificada - PetCare",
                "Su hora para %s fue cambiada al %s a las %s con %s.");
    }

    /** Envia un recordatorio por cada reserva PENDIENTE o CONFIRMADA de manana. Devuelve las notificaciones creadas. */
    @Transactional
    public List<NotificacionResponse> enviarRecordatorios() {
        LocalDate manana = LocalDate.now().plusDays(1);
        List<Reserva> reservas = new ArrayList<>(reservaRepository.findByFechaAndEstado(manana, EstadoReserva.CONFIRMADA));
        reservas.addAll(reservaRepository.findByFechaAndEstado(manana, EstadoReserva.PENDIENTE));
        return reservas.stream()
                .map(r -> notificar(r, TipoNotificacion.RECORDATORIO, "Recordatorio de hora - PetCare",
                        "Le recordamos su hora para %s manana %s a las %s con %s."))
                .map(NotificacionResponse::de)
                .toList();
    }

    /** Avisa a los duenos de las mascotas cuya proxima dosis de vacuna vence en los proximos "dias" dias. */
    @Transactional
    public List<NotificacionResponse> enviarRecordatoriosVacunas(int dias) {
        if (dias < 0) {
            throw new ReglaNegocioException("El parametro dias no puede ser negativo");
        }
        LocalDate hoy = LocalDate.now();
        return vacunaRepository.findByFechaProximaDosisBetween(hoy, hoy.plusDays(dias)).stream()
                .map(this::notificarVacuna)
                .map(NotificacionResponse::de)
                .toList();
    }

    // ------------------------------------------------------------------ consultas / reenvio

    public List<NotificacionResponse> listar(EstadoNotificacion estado) {
        List<Notificacion> lista = estado == null
                ? notificacionRepository.findAllByOrderByCreadoEnDesc()
                : notificacionRepository.findByEstadoOrderByCreadoEnDesc(estado);
        return lista.stream().map(NotificacionResponse::de).toList();
    }

    public List<NotificacionResponse> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", clienteId);
        }
        return notificacionRepository.findByClienteIdOrderByCreadoEnDesc(clienteId).stream()
                .map(NotificacionResponse::de).toList();
    }

    /** Vuelve a enviar una notificacion que no se pudo entregar. */
    @Transactional
    public NotificacionResponse reenviar(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion", id));
        if (n.getEstado() == EstadoNotificacion.ENVIADA) {
            throw new ReglaNegocioException("La notificacion " + id + " ya fue enviada");
        }
        enviar(n);
        return NotificacionResponse.de(notificacionRepository.save(n));
    }

    // ------------------------------------------------------------------ internos

    private Notificacion notificar(Reserva r, TipoNotificacion tipo, String asunto, String plantilla) {
        Cliente cliente = r.getMascota().getCliente();
        String mensaje = "Hola " + cliente.getNombre() + ". " + String.format(plantilla,
                r.getMascota().getNombre(), r.getFecha().format(FECHA), r.getHora().format(HORA),
                r.getVeterinario().getNombre() + " " + r.getVeterinario().getApellido());

        Notificacion n = new Notificacion();
        n.setCliente(cliente);
        n.setReserva(r);
        n.setCanal(CanalNotificacion.EMAIL);
        n.setTipo(tipo);
        n.setDestinatario(cliente.getEmail());
        n.setAsunto(asunto);
        n.setMensaje(mensaje);
        enviar(n);
        return notificacionRepository.save(n);
    }

    private Notificacion notificarVacuna(Vacuna v) {
        Cliente cliente = v.getMascota().getCliente();
        Notificacion n = new Notificacion();
        n.setCliente(cliente);
        n.setCanal(CanalNotificacion.EMAIL);
        n.setTipo(TipoNotificacion.RECORDATORIO);
        n.setDestinatario(cliente.getEmail());
        n.setAsunto("Recordatorio de vacuna - PetCare");
        n.setMensaje("Hola " + cliente.getNombre() + ". La proxima dosis de la vacuna " + v.getNombre()
                + " de " + v.getMascota().getNombre() + " vence el " + v.getFechaProximaDosis().format(FECHA)
                + ". Reserve su hora en PetCare.");
        enviar(n);
        return notificacionRepository.save(n);
    }

    /** Llama a la API externa y deja el resultado (ENVIADA/FALLIDA) en la notificacion. */
    private void enviar(Notificacion n) {
        try {
            RespuestaEnvioDto resp = switch (n.getCanal()) {
                case EMAIL -> notificacionApiClient.enviarEmail(n.getDestinatario(), n.getAsunto(), n.getMensaje());
                case SMS, WHATSAPP -> notificacionApiClient.enviarSms(n.getDestinatario(), n.getMensaje());
            };
            n.setEstado(EstadoNotificacion.ENVIADA);
            n.setEnviadoEn(LocalDateTime.now());
            n.setRespuestaApi(resp != null ? "id=" + resp.id() + " estado=" + resp.estado() : "sin respuesta");
        } catch (ApiExternaException e) {
            log.warn("Fallo el envio de la notificacion a {}: {}", n.getDestinatario(), e.getMessage());
            n.setEstado(EstadoNotificacion.FALLIDA);
            n.setRespuestaApi(recortar(e.getMessage(), 500));
        }
    }

    private static String recortar(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
