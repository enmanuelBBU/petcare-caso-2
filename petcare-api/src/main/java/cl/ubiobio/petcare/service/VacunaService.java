package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.VacunaRequest;
import cl.ubiobio.petcare.dto.VacunaResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Mascota;
import cl.ubiobio.petcare.model.entity.Vacuna;
import cl.ubiobio.petcare.model.entity.Veterinario;
import cl.ubiobio.petcare.repository.MascotaRepository;
import cl.ubiobio.petcare.repository.VacunaRepository;
import cl.ubiobio.petcare.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Carnet de vacunacion. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacunaService {

    private final VacunaRepository vacunaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public VacunaResponse buscarPorId(Long id) {
        return VacunaResponse.de(obtener(id));
    }

    /** Carnet de la mascota, de la vacuna mas reciente a la mas antigua. */
    public List<VacunaResponse> listarPorMascota(Long mascotaId) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new ResourceNotFoundException("Mascota", mascotaId);
        }
        return vacunaRepository.findByMascotaIdOrderByFechaAplicacionDesc(mascotaId).stream()
                .map(VacunaResponse::de).toList();
    }

    /** Vacunas cuya proxima dosis vence entre hoy y dentro de "dias" dias (para recordatorios). */
    public List<VacunaResponse> proximasDosis(int dias) {
        if (dias < 0) {
            throw new ReglaNegocioException("El parametro dias no puede ser negativo");
        }
        LocalDate hoy = LocalDate.now();
        return vacunaRepository.findByFechaProximaDosisBetween(hoy, hoy.plusDays(dias)).stream()
                .sorted(Comparator.comparing(Vacuna::getFechaProximaDosis))
                .map(VacunaResponse::de)
                .toList();
    }

    @Transactional
    public VacunaResponse registrar(VacunaRequest req) {
        Vacuna v = new Vacuna();
        copiarDatos(req, v);
        return VacunaResponse.de(vacunaRepository.save(v));
    }

    @Transactional
    public VacunaResponse actualizar(Long id, VacunaRequest req) {
        Vacuna v = obtener(id);
        copiarDatos(req, v);
        return VacunaResponse.de(vacunaRepository.save(v));
    }

    @Transactional
    public void eliminar(Long id) {
        vacunaRepository.delete(obtener(id));
    }

    private Vacuna obtener(Long id) {
        return vacunaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vacuna", id));
    }

    private void copiarDatos(VacunaRequest req, Vacuna v) {
        if (req.fechaProximaDosis() != null && !req.fechaProximaDosis().isAfter(req.fechaAplicacion())) {
            throw new ReglaNegocioException("La fecha de la proxima dosis debe ser posterior a la fecha de aplicacion");
        }
        Mascota mascota = mascotaRepository.findById(req.mascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", req.mascotaId()));
        Veterinario veterinario = null;
        if (req.veterinarioId() != null) {
            veterinario = veterinarioRepository.findById(req.veterinarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veterinario", req.veterinarioId()));
        }
        v.setMascota(mascota);
        v.setVeterinario(veterinario);
        v.setNombre(req.nombre().trim());
        v.setLote(req.lote());
        v.setFechaAplicacion(req.fechaAplicacion());
        v.setFechaProximaDosis(req.fechaProximaDosis());
    }
}
