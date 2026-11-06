package cl.ubiobio.petcare.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente de la API externa de geolocalizacion (apis-externas, Node).
 *   GET {base-url}/api/geo/ubicacion?lat={lat}&lng={lng}
 *   GET {base-url}/api/geo/geocodificar?direccion={direccion}
 */
@Component
@RequiredArgsConstructor
public class GeolocalizacionApiClient {

    private final RestClient apisExternasRestClient;

    // TODO: obtenerUbicacion(BigDecimal lat, BigDecimal lng) -> UbicacionDto (direccion, urlMapa)
    // TODO: geocodificar(String direccion) -> UbicacionDto (lat, lng)
}
