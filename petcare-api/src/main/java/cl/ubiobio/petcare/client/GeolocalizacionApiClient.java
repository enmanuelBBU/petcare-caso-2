package cl.ubiobio.petcare.client;

import cl.ubiobio.petcare.dto.UbicacionDto;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

/**
 * Cliente de la API externa de geolocalizacion (apis-externas, Node).
 *   GET {base-url}/api/geo/ubicacion?lat={lat}&lng={lng}
 *   GET {base-url}/api/geo/geocodificar?direccion={direccion}
 */
@Component
@RequiredArgsConstructor
public class GeolocalizacionApiClient {

    private final RestClient apisExternasRestClient;

    /** URL del mapa (OpenStreetMap) y URL para embeberlo a partir de las coordenadas. */
    public UbicacionDto obtenerUbicacion(BigDecimal lat, BigDecimal lng) {
        try {
            return apisExternasRestClient.get()
                    .uri(b -> b.path("/api/geo/ubicacion")
                            .queryParam("lat", lat.toPlainString())
                            .queryParam("lng", lng.toPlainString())
                            .build())
                    .retrieve()
                    .body(UbicacionDto.class);
        } catch (RestClientException e) {
            throw new ApiExternaException("No se pudo consultar la API de geolocalizacion: " + e.getMessage(), e);
        }
    }

    /** Coordenadas de una direccion (la API Node consulta Nominatim / OpenStreetMap). */
    public UbicacionDto geocodificar(String direccion) {
        try {
            return apisExternasRestClient.get()
                    .uri(b -> b.path("/api/geo/geocodificar").queryParam("direccion", direccion).build())
                    .retrieve()
                    .body(UbicacionDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("No se encontraron coordenadas para la direccion: " + direccion);
        } catch (RestClientException e) {
            throw new ApiExternaException("No se pudo consultar la API de geolocalizacion: " + e.getMessage(), e);
        }
    }
}
