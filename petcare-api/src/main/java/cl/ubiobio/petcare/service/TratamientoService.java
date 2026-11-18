package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.MedicamentoExternoDto;
import cl.ubiobio.petcare.dto.MedicamentoPrescritoRequest;
import cl.ubiobio.petcare.dto.TratamientoActualizarRequest;
import cl.ubiobio.petcare.dto.TratamientoRequest;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Atencion;
import cl.ubiobio.petcare.model.entity.MedicamentoPrescrito;
import cl.ubiobio.petcare.model.entity.Tratamiento;
import cl.ubiobio.petcare.repository.AtencionRepository;
import cl.ubiobio.petcare.repository.MedicamentoPrescritoRepository;
import cl.ubiobio.petcare.repository.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Tratamientos y medicamentos prescritos (los datos del medicamento vienen de la API externa). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;
    private final AtencionRepository atencionRepository;
    private final MedicamentoPrescritoRepository medicamentoPrescritoRepository;
    private final MedicamentoService medicamentoService;

    public TratamientoResponse buscarPorId(Long id) {
        return TratamientoResponse.de(obtener(id));
    }

    public List<TratamientoResponse> listarPorMascota(Long mascotaId) {
        return tratamientoRepository.findByAtencionMascotaId(mascotaId).stream().map(TratamientoResponse::de).toList();
    }

    @Transactional
    public TratamientoResponse crear(TratamientoRequest req) {
        validarFechas(req.fechaInicio(), req.fechaFin());
        Atencion atencion = atencionRepository.findById(req.atencionId())
                .orElseThrow(() -> new ResourceNotFoundException("Atencion", req.atencionId()));

        Tratamiento t = new Tratamiento();
        t.setAtencion(atencion);
        t.setDescripcion(req.descripcion().trim());
        t.setIndicaciones(req.indicaciones());
        t.setFechaInicio(req.fechaInicio());
        t.setFechaFin(req.fechaFin());
        atencion.getTratamientos().add(t);

        if (req.medicamentos() != null) {
            req.medicamentos().forEach(m -> t.getMedicamentos().add(prescribir(t, m)));
        }
        return TratamientoResponse.de(tratamientoRepository.saveAndFlush(t));
    }

    @Transactional
    public TratamientoResponse actualizar(Long id, TratamientoActualizarRequest req) {
        validarFechas(req.fechaInicio(), req.fechaFin());
        Tratamiento t = obtener(id);
        t.setDescripcion(req.descripcion().trim());
        t.setIndicaciones(req.indicaciones());
        t.setFechaInicio(req.fechaInicio());
        t.setFechaFin(req.fechaFin());
        return TratamientoResponse.de(tratamientoRepository.saveAndFlush(t));
    }

    /** Agrega un medicamento al tratamiento consultando la API externa por codigo o nombre. */
    @Transactional
    public TratamientoResponse agregarMedicamento(Long tratamientoId, MedicamentoPrescritoRequest req) {
        Tratamiento t = obtener(tratamientoId);
        t.getMedicamentos().add(prescribir(t, req));
        return TratamientoResponse.de(tratamientoRepository.saveAndFlush(t));
    }

    @Transactional
    public TratamientoResponse quitarMedicamento(Long tratamientoId, Long medicamentoId) {
        Tratamiento t = obtener(tratamientoId);
        boolean quitado = t.getMedicamentos().removeIf(m -> m.getId().equals(medicamentoId));
        if (!quitado) {
            throw new ResourceNotFoundException("El medicamento " + medicamentoId
                    + " no pertenece al tratamiento " + tratamientoId);
        }
        return TratamientoResponse.de(tratamientoRepository.saveAndFlush(t));
    }

    Tratamiento obtener(Long id) {
        return tratamientoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tratamiento", id));
    }

    private MedicamentoPrescrito prescribir(Tratamiento t, MedicamentoPrescritoRequest req) {
        MedicamentoExternoDto externo;
        if (req.codigoExterno() != null && !req.codigoExterno().isBlank()) {
            externo = medicamentoService.obtenerDetalle(req.codigoExterno().trim());
        } else if (req.nombre() != null && !req.nombre().isBlank()) {
            externo = medicamentoService.resolverPorNombre(req.nombre());
        } else {
            throw new ReglaNegocioException("Cada medicamento debe indicar codigoExterno o nombre");
        }

        MedicamentoPrescrito m = new MedicamentoPrescrito();
        m.setTratamiento(t);
        m.setCodigoExterno(externo.codigo());
        m.setNombre(externo.nombre());
        m.setPrincipioActivo(externo.principioActivo());
        m.setDosis(req.dosis().trim());
        m.setFrecuencia(req.frecuencia().trim());
        m.setDuracionDias(req.duracionDias());
        m.setViaAdministracion(req.viaAdministracion());
        return m;
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (fin != null && fin.isBefore(inicio)) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}
