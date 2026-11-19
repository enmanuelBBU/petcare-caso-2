package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.DisponibilidadResponse;
import cl.ubiobio.petcare.dto.HorarioRequest;
import cl.ubiobio.petcare.dto.HorarioResponse;
import cl.ubiobio.petcare.dto.VeterinarioRequest;
import cl.ubiobio.petcare.dto.VeterinarioResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Clinica;
import cl.ubiobio.petcare.model.entity.HorarioVeterinario;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.entity.Veterinario;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.ClinicaRepository;
import cl.ubiobio.petcare.repository.HorarioVeterinarioRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** Gestion de veterinarios, sus horarios y disponibilidad. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VeterinarioService {

    /** Duracion de cada hora reservable dentro de un bloque de atencion. */
    static final int INTERVALO_MINUTOS = 30;

    private final VeterinarioRepository veterinarioRepository;
    private final HorarioVeterinarioRepository horarioVeterinarioRepository;
    private final ReservaRepository reservaRepository;
    private final AtencionRepository atencionRepository;
    private final ClinicaRepository clinicaRepository;

    // ------------------------------------------------------------------ CRUD

    /** Por defecto solo los activos; especialidad filtra por coincidencia parcial. */
    public List<VeterinarioResponse> listar(String especialidad, boolean incluirInactivos) {
        boolean filtrar = especialidad != null && !especialidad.isBlank();
        List<Veterinario> lista;
        if (incluirInactivos) {
            lista = filtrar ? veterinarioRepository.findByEspecialidadContainingIgnoreCase(especialidad.trim())
                    : veterinarioRepository.findAll();
        } else {
            lista = filtrar ? veterinarioRepository.findByActivoTrueAndEspecialidadContainingIgnoreCase(especialidad.trim())
                    : veterinarioRepository.findByActivoTrue();
        }
        return lista.stream().map(VeterinarioResponse::de).toList();
    }

    public VeterinarioResponse buscarPorId(Long id) {
        return VeterinarioResponse.de(obtener(id));
    }

    @Transactional
    public VeterinarioResponse crear(VeterinarioRequest req) {
        if (veterinarioRepository.existsByRut(req.rut().toUpperCase())) {
            throw new RegistroDuplicadoException("Ya existe un veterinario con RUT " + req.rut());
        }
        if (veterinarioRepository.existsByEmail(req.email().trim().toLowerCase())) {
            throw new RegistroDuplicadoException("Ya existe un veterinario con email " + req.email());
        }
        Veterinario v = new Veterinario();
        copiarDatos(req, v);
        v.setActivo(true);
        return VeterinarioResponse.de(veterinarioRepository.saveAndFlush(v));
    }

    @Transactional
    public VeterinarioResponse actualizar(Long id, VeterinarioRequest req) {
        Veterinario v = obtener(id);
        if (veterinarioRepository.existsByRutAndIdNot(req.rut().toUpperCase(), id)) {
            throw new RegistroDuplicadoException("Ya existe otro veterinario con RUT " + req.rut());
        }
        if (veterinarioRepository.existsByEmailAndIdNot(req.email().trim().toLowerCase(), id)) {
            throw new RegistroDuplicadoException("Ya existe otro veterinario con email " + req.email());
        }
        copiarDatos(req, v);
        if (req.activo() != null) {
            v.setActivo(req.activo());
        }
        return VeterinarioResponse.de(veterinarioRepository.saveAndFlush(v));
    }

    /**
     * Si el veterinario ya tiene reservas o atenciones se da de baja (activo = false) para no
     * perder el historial clinico; si no tiene, se elimina junto con sus horarios.
     */
    @Transactional
    public void eliminar(Long id) {
        Veterinario v = obtener(id);
        if (reservaRepository.existsByVeterinarioId(id) || atencionRepository.existsByVeterinarioId(id)) {
            v.setActivo(false);
            veterinarioRepository.save(v);
        } else {
            veterinarioRepository.delete(v);
        }
    }

    // ------------------------------------------------------------------ horarios

    public List<HorarioResponse> listarHorarios(Long veterinarioId) {
        obtener(veterinarioId);
        return horarioVeterinarioRepository.findByVeterinarioId(veterinarioId).stream()
                .sorted(Comparator.comparing(HorarioVeterinario::getDiaSemana)
                        .thenComparing(HorarioVeterinario::getHoraInicio))
                .map(HorarioResponse::de)
                .toList();
    }

    /** Agrega un bloque semanal; no puede traslaparse con otro bloque del mismo dia. */
    @Transactional
    public HorarioResponse agregarHorario(Long veterinarioId, HorarioRequest req) {
        Veterinario v = obtener(veterinarioId);
        if (!req.horaFin().isAfter(req.horaInicio())) {
            throw new ReglaNegocioException("La hora de fin debe ser posterior a la hora de inicio");
        }
        boolean traslape = horarioVeterinarioRepository
                .findByVeterinarioIdAndDiaSemana(veterinarioId, req.diaSemana()).stream()
                .anyMatch(b -> req.horaInicio().isBefore(b.getHoraFin()) && b.getHoraInicio().isBefore(req.horaFin()));
        if (traslape) {
            throw new RegistroDuplicadoException("El bloque " + req.horaInicio() + "-" + req.horaFin()
                    + " se traslapa con otro bloque del mismo dia");
        }
        HorarioVeterinario h = new HorarioVeterinario();
        h.setVeterinario(v);
        h.setDiaSemana(req.diaSemana());
        h.setHoraInicio(req.horaInicio());
        h.setHoraFin(req.horaFin());
        return HorarioResponse.de(horarioVeterinarioRepository.save(h));
    }

    @Transactional
    public void quitarHorario(Long veterinarioId, Long horarioId) {
        HorarioVeterinario h = horarioVeterinarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", horarioId));
        if (!h.getVeterinario().getId().equals(veterinarioId)) {
            throw new ResourceNotFoundException("El horario " + horarioId + " no pertenece al veterinario " + veterinarioId);
        }
        horarioVeterinarioRepository.delete(h);
    }

    // ------------------------------------------------------------------ disponibilidad

    /**
     * Divide los bloques del dia de la semana en horas cada INTERVALO_MINUTOS y descuenta las
     * horas ocupadas por reservas no canceladas. Si la fecha es hoy, se omiten las horas ya pasadas.
     */
    public DisponibilidadResponse calcularDisponibilidad(Long veterinarioId, LocalDate fecha) {
        Veterinario v = obtener(veterinarioId);
        List<HorarioVeterinario> bloques = horarioVeterinarioRepository
                .findByVeterinarioIdAndDiaSemana(veterinarioId, fecha.getDayOfWeek().getValue()).stream()
                .sorted(Comparator.comparing(HorarioVeterinario::getHoraInicio))
                .toList();

        Set<LocalTime> ocupadas = reservaRepository
                .findByVeterinarioIdAndFechaAndEstadoNot(veterinarioId, fecha, EstadoReserva.CANCELADA).stream()
                .map(Reserva::getHora)
                .collect(Collectors.toCollection(TreeSet::new));

        LocalDateTime ahora = LocalDateTime.now();
        List<LocalTime> disponibles = new ArrayList<>();
        if (Boolean.TRUE.equals(v.getActivo())) {
            for (HorarioVeterinario b : bloques) {
                LocalTime t = b.getHoraInicio();
                while (t.isBefore(b.getHoraFin())) {
                    if (!ocupadas.contains(t) && LocalDateTime.of(fecha, t).isAfter(ahora)) {
                        disponibles.add(t);
                    }
                    LocalTime siguiente = t.plusMinutes(INTERVALO_MINUTOS);
                    if (siguiente.isBefore(t)) {
                        break; // cruzo la medianoche
                    }
                    t = siguiente;
                }
            }
        }
        return new DisponibilidadResponse(v.getId(), v.getNombre() + " " + v.getApellido(), fecha, INTERVALO_MINUTOS,
                bloques.stream().map(HorarioResponse::de).toList(), disponibles, new ArrayList<>(ocupadas));
    }

    // ------------------------------------------------------------------ internos

    Veterinario obtener(Long id) {
        return veterinarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Veterinario", id));
    }

    private void copiarDatos(VeterinarioRequest req, Veterinario v) {
        v.setRut(req.rut().toUpperCase());
        v.setNombre(req.nombre().trim());
        v.setApellido(req.apellido().trim());
        v.setEmail(req.email().trim().toLowerCase());
        v.setTelefono(req.telefono());
        v.setEspecialidad(req.especialidad());
        v.setClinica(resolverClinica(req.clinicaId()));
    }

    private Clinica resolverClinica(Long clinicaId) {
        if (clinicaId == null) {
            return null;
        }
        return clinicaRepository.findById(clinicaId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinica", clinicaId));
    }
}
