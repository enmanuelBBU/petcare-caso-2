package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.client.GeolocalizacionApiClient;
import cl.ubiobio.petcare.dto.ClinicaRequest;
import cl.ubiobio.petcare.dto.ClinicaResponse;
import cl.ubiobio.petcare.dto.UbicacionClinicaResponse;
import cl.ubiobio.petcare.dto.UbicacionDto;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.model.entity.Clinica;
import cl.ubiobio.petcare.repository.ClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Datos de la clinica y geolocalizacion. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;
    private final GeolocalizacionApiClient geolocalizacionApiClient;

    public List<ClinicaResponse> listar() {
        return clinicaRepository.findAll().stream().map(ClinicaResponse::de).toList();
    }

    public ClinicaResponse buscarPorId(Long id) {
        return ClinicaResponse.de(obtener(id));
    }

    @Transactional
    public ClinicaResponse actualizar(Long id, ClinicaRequest req) {
        if ((req.latitud() == null) != (req.longitud() == null)) {
            throw new ReglaNegocioException("Debe indicar latitud y longitud juntas");
        }
        if (req.horaApertura() != null && req.horaCierre() != null && !req.horaCierre().isAfter(req.horaApertura())) {
            throw new ReglaNegocioException("La hora de cierre debe ser posterior a la de apertura");
        }
        Clinica c = obtener(id);
        boolean cambioDireccion = !Objects.equals(c.getDireccion(), req.direccion().trim())
                || !Objects.equals(c.getComuna(), req.comuna())
                || !Objects.equals(c.getCiudad(), req.ciudad().trim());

        c.setNombre(req.nombre().trim());
        c.setDireccion(req.direccion().trim());
        c.setComuna(req.comuna());
        c.setCiudad(req.ciudad().trim());
        c.setTelefono(req.telefono());
        c.setEmail(req.email());
        c.setHoraApertura(req.horaApertura());
        c.setHoraCierre(req.horaCierre());
        if (req.latitud() != null) {
            c.setLatitud(req.latitud());
            c.setLongitud(req.longitud());
        } else if (cambioDireccion) {
            // las coordenadas antiguas ya no sirven: se recalculan al consultar la ubicacion
            c.setLatitud(null);
            c.setLongitud(null);
        }
        return ClinicaResponse.de(clinicaRepository.save(c));
    }

    /**
     * Coordenadas + URL de mapa desde la API externa.
     * Si la clinica aun no tiene coordenadas, se geocodifica su direccion y se guardan.
     */
    @Transactional
    public UbicacionClinicaResponse obtenerUbicacion(Long id) {
        Clinica c = obtener(id);
        if (c.getLatitud() == null || c.getLongitud() == null) {
            String direccion = Stream.of(c.getDireccion(), c.getComuna(), c.getCiudad(), "Chile")
                    .filter(s -> s != null && !s.isBlank())
                    .collect(Collectors.joining(", "));
            UbicacionDto geo = geolocalizacionApiClient.geocodificar(direccion);
            c.setLatitud(geo.latitud().setScale(7, RoundingMode.HALF_UP));
            c.setLongitud(geo.longitud().setScale(7, RoundingMode.HALF_UP));
            clinicaRepository.save(c);
            return UbicacionClinicaResponse.de(c, geo);
        }
        return UbicacionClinicaResponse.de(c, geolocalizacionApiClient.obtenerUbicacion(c.getLatitud(), c.getLongitud()));
    }

    /** Busca las coordenadas de cualquier direccion (sin guardarlas). */
    public UbicacionDto geocodificar(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaNegocioException("Debe indicar una direccion");
        }
        return geolocalizacionApiClient.geocodificar(direccion.trim());
    }

    private Clinica obtener(Long id) {
        return clinicaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Clinica", id));
    }
}
