package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.dto.TipoServicioRequest;
import cl.ubiobio.petcare.dto.TipoServicioResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.TipoServicio;
import cl.ubiobio.petcare.repository.TipoServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Catalogo de servicios con duracion y precio. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;

    /** Por defecto solo los servicios activos (los que se pueden reservar). */
    public List<TipoServicioResponse> listar(boolean incluirInactivos) {
        List<TipoServicio> lista = incluirInactivos
                ? tipoServicioRepository.findAll()
                : tipoServicioRepository.findByActivoTrue();
        return lista.stream().map(TipoServicioResponse::de).toList();
    }

    public TipoServicioResponse buscarPorId(Long id) {
        return TipoServicioResponse.de(obtener(id));
    }

    @Transactional
    public TipoServicioResponse crear(TipoServicioRequest req) {
        if (tipoServicioRepository.existsByNombreIgnoreCase(req.nombre().trim())) {
            throw new RegistroDuplicadoException("Ya existe un servicio llamado " + req.nombre());
        }
        TipoServicio t = new TipoServicio();
        copiarDatos(req, t);
        t.setActivo(req.activo() == null || req.activo());
        return TipoServicioResponse.de(tipoServicioRepository.save(t));
    }

    @Transactional
    public TipoServicioResponse actualizar(Long id, TipoServicioRequest req) {
        TipoServicio t = obtener(id);
        if (tipoServicioRepository.existsByNombreIgnoreCaseAndIdNot(req.nombre().trim(), id)) {
            throw new RegistroDuplicadoException("Ya existe otro servicio llamado " + req.nombre());
        }
        copiarDatos(req, t);
        if (req.activo() != null) {
            t.setActivo(req.activo());
        }
        return TipoServicioResponse.de(tipoServicioRepository.save(t));
    }

    /**
     * Baja logica: el servicio deja de ofrecerse pero las reservas y pagos
     * que lo referencian conservan su informacion.
     */
    @Transactional
    public void desactivar(Long id) {
        TipoServicio t = obtener(id);
        t.setActivo(false);
        tipoServicioRepository.save(t);
    }

    private TipoServicio obtener(Long id) {
        return tipoServicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio", id));
    }

    private void copiarDatos(TipoServicioRequest req, TipoServicio t) {
        t.setNombre(req.nombre().trim());
        t.setDescripcion(req.descripcion());
        t.setDuracionMinutos(req.duracionMinutos());
        t.setPrecio(req.precio());
    }
}
