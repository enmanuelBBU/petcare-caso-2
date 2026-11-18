package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.AtencionRequest;
import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Atencion;
import cl.ubiobio.petcare.model.entity.Mascota;
import cl.ubiobio.petcare.model.entity.Reserva;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.ReservaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Fichas clinicas (atenciones): conforman el historial medico de cada mascota. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AtencionService {

    private final AtencionRepository atencionRepository;
    private final ReservaRepository reservaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ReservaService reservaService;

    public AtencionResponse buscarPorId(Long id) {
        return AtencionResponse.de(obtener(id));
    }

    /** Historial clinico de la mascota: atenciones (con tratamientos y medicamentos), de la mas reciente a la mas antigua. */
    public List<AtencionResponse> historialPorMascota(Long mascotaId) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new ResourceNotFoundException("Mascota", mascotaId);
        }
        return atencionRepository.findByMascotaIdOrderByFechaAtencionDesc(mascotaId).stream()
                .map(AtencionResponse::de).toList();
    }

    public List<TratamientoResponse> tratamientosDeAtencion(Long atencionId) {
        return obtener(atencionId).getTratamientos().stream().map(TratamientoResponse::de).toList();
    }

    @Transactional
    public AtencionResponse registrar(AtencionRequest req) {
        Atencion atencion = new Atencion();

        if (req.reservaId() != null) {
            Reserva reserva = reservaRepository.findById(req.reservaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva", req.reservaId()));
            if (atencionRepository.findByReservaId(reserva.getId()).isPresent()) {
                throw new RegistroDuplicadoException("La reserva " + reserva.getId() + " ya tiene una atencion registrada");
            }
            if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
                throw new ReglaNegocioException("Solo se puede atender una reserva CONFIRMADA (estado actual: "
                        + reserva.getEstado() + ")");
            }
            atencion.setReserva(reserva);
            atencion.setMascota(reserva.getMascota());
            atencion.setVeterinario(reserva.getVeterinario());
            if (req.motivoConsulta() == null) {
                atencion.setMotivoConsulta(reserva.getMotivo());
            }
            reservaService.marcarAtendida(reserva.getId(), "Atencion registrada");
        } else {
            if (req.mascotaId() == null || req.veterinarioId() == null) {
                throw new ReglaNegocioException("Sin reservaId se deben indicar mascotaId y veterinarioId");
            }
            atencion.setMascota(mascotaRepository.findById(req.mascotaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mascota", req.mascotaId())));
            atencion.setVeterinario(veterinarioRepository.findById(req.veterinarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinario", req.veterinarioId())));
        }

        atencion.setFechaAtencion(LocalDateTime.now());
        copiarDatos(req, atencion);
        actualizarPesoMascota(atencion);
        return AtencionResponse.de(atencionRepository.saveAndFlush(atencion));
    }

    /** Actualiza los datos clinicos (diagnostico, observaciones, signos); no cambia mascota, veterinario ni reserva. */
    @Transactional
    public AtencionResponse actualizar(Long id, AtencionRequest req) {
        Atencion atencion = obtener(id);
        copiarDatos(req, atencion);
        actualizarPesoMascota(atencion);
        return AtencionResponse.de(atencionRepository.saveAndFlush(atencion));
    }

    Atencion obtener(Long id) {
        return atencionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Atencion", id));
    }

    private void copiarDatos(AtencionRequest req, Atencion a) {
        if (req.motivoConsulta() != null) {
            a.setMotivoConsulta(req.motivoConsulta());
        }
        a.setAnamnesis(req.anamnesis());
        a.setDiagnostico(req.diagnostico().trim());
        a.setPesoRegistrado(req.pesoRegistrado());
        a.setTemperatura(req.temperatura());
        a.setObservaciones(req.observaciones());
    }

    /** El peso medido en la consulta pasa a ser el peso actual de la mascota. */
    private void actualizarPesoMascota(Atencion a) {
        if (a.getPesoRegistrado() != null) {
            Mascota m = a.getMascota();
            m.setPeso(a.getPesoRegistrado());
        }
    }
}
