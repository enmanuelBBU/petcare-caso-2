package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.HistorialEstadoResponse;
import cl.ubiobio.petcare.dto.ReservaModificarRequest;
import cl.ubiobio.petcare.dto.ReservaRequest;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Clinica;
import cl.ubiobio.petcare.model.entity.HistorialEstadoReserva;
import cl.ubiobio.petcare.model.entity.HorarioVeterinario;
import cl.ubiobio.petcare.model.entity.Mascota;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.entity.TipoServicio;
import cl.ubiobio.petcare.model.entity.Veterinario;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.repository.ClienteRepository;
import cl.ubiobio.petcare.repository.ClinicaRepository;
import cl.ubiobio.petcare.repository.HistorialEstadoReservaRepository;
import cl.ubiobio.petcare.repository.HorarioVeterinarioRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.TipoServicioRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/** Reglas de negocio de las horas medicas. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final HorarioVeterinarioRepository horarioVeterinarioRepository;
    private final HistorialEstadoReservaRepository historialEstadoReservaRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final ClinicaRepository clinicaRepository;
    private final ClienteRepository clienteRepository;
    private final NotificacionService notificacionService;

    // ------------------------------------------------------------------ consultas

    public List<ReservaResponse> listar(EstadoReserva estado, LocalDate fecha, Long veterinarioId) {
        return reservaRepository.buscar(estado, fecha, veterinarioId).stream().map(ReservaResponse::de).toList();
    }

    public ReservaResponse buscarPorId(Long id) {
        return ReservaResponse.de(obtener(id));
    }

    public List<ReservaResponse> listarPorMascota(Long mascotaId) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new ResourceNotFoundException("Mascota", mascotaId);
        }
        return reservaRepository.findByMascotaIdOrderByFechaDescHoraDesc(mascotaId).stream()
                .map(ReservaResponse::de).toList();
    }

    public List<ReservaResponse> listarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", clienteId);
        }
        return reservaRepository.findByMascotaClienteIdOrderByFechaDescHoraDesc(clienteId).stream()
                .map(ReservaResponse::de).toList();
    }

    public List<HistorialEstadoResponse> historialEstados(Long reservaId) {
        obtener(reservaId);
        return historialEstadoReservaRepository.findByReservaIdOrderByFechaCambioAsc(reservaId).stream()
                .map(HistorialEstadoResponse::de).toList();
    }

    // ------------------------------------------------------------------ crear / modificar

    @Transactional
    public ReservaResponse crear(ReservaRequest req) {
        Mascota mascota = mascotaRepository.findById(req.mascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", req.mascotaId()));
        if (!Boolean.TRUE.equals(mascota.getActivo())) {
            throw new ReglaNegocioException("La mascota " + mascota.getNombre() + " no esta activa");
        }
        Veterinario veterinario = obtenerVeterinario(req.veterinarioId());

        validarHorario(veterinario, req.fecha(), req.hora(), mascota.getId(), null);

        Reserva reserva = new Reserva();
        reserva.setMascota(mascota);
        reserva.setVeterinario(veterinario);
        reserva.setTipoServicio(obtenerTipoServicio(req.tipoServicioId()));
        reserva.setClinica(resolverClinica(req.clinicaId(), veterinario));
        reserva.setFecha(req.fecha());
        reserva.setHora(req.hora());
        reserva.setMotivo(req.motivo());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva = reservaRepository.saveAndFlush(reserva);

        registrarHistorial(reserva, null, EstadoReserva.PENDIENTE, "Reserva creada");
        return ReservaResponse.de(reserva);
    }

    /** Cambia fecha/hora (y opcionalmente veterinario, servicio o motivo) re-validando duplicados. */
    @Transactional
    public ReservaResponse modificar(Long id, ReservaModificarRequest req) {
        Reserva reserva = obtener(id);
        if (!Set.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA).contains(reserva.getEstado())) {
            throw new ReglaNegocioException("No se puede modificar una reserva " + reserva.getEstado());
        }
        Veterinario veterinario = req.veterinarioId() != null
                ? obtenerVeterinario(req.veterinarioId())
                : reserva.getVeterinario();

        validarHorario(veterinario, req.fecha(), req.hora(), reserva.getMascota().getId(), reserva.getId());

        reserva.setVeterinario(veterinario);
        reserva.setFecha(req.fecha());
        reserva.setHora(req.hora());
        if (req.tipoServicioId() != null) {
            reserva.setTipoServicio(obtenerTipoServicio(req.tipoServicioId()));
        }
        if (req.motivo() != null) {
            reserva.setMotivo(req.motivo());
        }
        reserva = reservaRepository.saveAndFlush(reserva);
        notificacionService.notificarModificacion(reserva);
        return ReservaResponse.de(reserva);
    }

    // ------------------------------------------------------------------ cambios de estado

    /** PENDIENTE -> CONFIRMADA (notifica al cliente) */
    @Transactional
    public ReservaResponse confirmar(Long id, String motivo) {
        Reserva reserva = cambiarEstado(id, EstadoReserva.CONFIRMADA, Set.of(EstadoReserva.PENDIENTE),
                motivo != null ? motivo : "Reserva confirmada");
        notificacionService.notificarConfirmacion(reserva);
        return ReservaResponse.de(reserva);
    }

    /** PENDIENTE | CONFIRMADA -> CANCELADA (libera el horario y notifica al cliente) */
    @Transactional
    public ReservaResponse cancelar(Long id, String motivo) {
        Reserva reserva = cambiarEstado(id, EstadoReserva.CANCELADA,
                Set.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA),
                motivo != null ? motivo : "Reserva cancelada");
        notificacionService.notificarCancelacion(reserva);
        return ReservaResponse.de(reserva);
    }

    /** CONFIRMADA -> ATENDIDA */
    @Transactional
    public ReservaResponse marcarAtendida(Long id, String motivo) {
        Reserva reserva = cambiarEstado(id, EstadoReserva.ATENDIDA, Set.of(EstadoReserva.CONFIRMADA),
                motivo != null ? motivo : "Atencion registrada");
        return ReservaResponse.de(reserva);
    }

    Reserva obtener(Long id) {
        return reservaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
    }

    private Reserva cambiarEstado(Long id, EstadoReserva nuevo, Set<EstadoReserva> permitidos, String motivo) {
        Reserva reserva = obtener(id);
        EstadoReserva anterior = reserva.getEstado();
        if (!permitidos.contains(anterior)) {
            throw new ReglaNegocioException("No se puede pasar una reserva de " + anterior + " a " + nuevo
                    + " (estados permitidos: " + permitidos + ")");
        }
        reserva.setEstado(nuevo);
        reserva = reservaRepository.saveAndFlush(reserva);
        registrarHistorial(reserva, anterior, nuevo, motivo);
        return reserva;
    }

    private void registrarHistorial(Reserva reserva, EstadoReserva anterior, EstadoReserva nuevo, String motivo) {
        HistorialEstadoReserva h = new HistorialEstadoReserva();
        h.setReserva(reserva);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setMotivo(motivo);
        historialEstadoReservaRepository.save(h);
    }

    // ------------------------------------------------------------------ validaciones

    /**
     * 1) la fecha/hora no puede ser pasada,
     * 2) debe caer dentro de un bloque de atencion del veterinario ese dia de la semana,
     * 3) ni el veterinario ni la mascota pueden tener otra reserva activa en el mismo dia y hora.
     * (La BD ademas lo garantiza con los indices unicos sobre slot_activo.)
     */
    private void validarHorario(Veterinario veterinario, LocalDate fecha, LocalTime hora,
                                Long mascotaId, Long excluirReservaId) {
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("No se puede reservar una hora que ya paso");
        }

        int diaSemana = fecha.getDayOfWeek().getValue();
        List<HorarioVeterinario> bloques =
                horarioVeterinarioRepository.findByVeterinarioIdAndDiaSemana(veterinario.getId(), diaSemana);
        boolean dentroDeHorario = bloques.stream()
                .anyMatch(b -> !hora.isBefore(b.getHoraInicio()) && hora.isBefore(b.getHoraFin()));
        if (!dentroDeHorario) {
            throw new ReglaNegocioException("El veterinario " + veterinario.getNombre() + " "
                    + veterinario.getApellido() + " no atiende el " + fecha + " a las " + hora);
        }

        if (reservaRepository.existeChoqueVeterinario(veterinario.getId(), fecha, hora,
                EstadoReserva.CANCELADA, excluirReservaId)) {
            throw new ReservaDuplicadaException("El veterinario ya tiene una reserva el " + fecha + " a las " + hora);
        }
        if (reservaRepository.existeChoqueMascota(mascotaId, fecha, hora,
                EstadoReserva.CANCELADA, excluirReservaId)) {
            throw new ReservaDuplicadaException("La mascota ya tiene una reserva el " + fecha + " a las " + hora);
        }
    }

    private Veterinario obtenerVeterinario(Long id) {
        Veterinario v = veterinarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", id));
        if (!Boolean.TRUE.equals(v.getActivo())) {
            throw new ReglaNegocioException("El veterinario " + v.getNombre() + " " + v.getApellido() + " no esta activo");
        }
        return v;
    }

    private TipoServicio obtenerTipoServicio(Long id) {
        if (id == null) {
            return null;
        }
        return tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio", id));
    }

    /** Si no se indica clinica, se usa la del veterinario. */
    private Clinica resolverClinica(Long clinicaId, Veterinario veterinario) {
        if (clinicaId == null) {
            return veterinario.getClinica();
        }
        return clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinica", clinicaId));
    }
}
