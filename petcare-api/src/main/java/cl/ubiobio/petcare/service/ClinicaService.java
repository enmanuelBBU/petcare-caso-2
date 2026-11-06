package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.ClinicaRepository;
import cl.ubiobio.petcare.client.GeolocalizacionApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Datos de la clinica y geolocalizacion. */
@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;
    private final GeolocalizacionApiClient geolocalizacionApiClient;

    // TODO: listar / buscarPorId / actualizar datos de la clinica
    // TODO: obtenerUbicacion(id): coordenadas + URL de mapa desde la API externa
}
