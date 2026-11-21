package cl.ubiobio.petcare.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Ping a la API externa (Node): GET {base-url}/api/estado. Nunca lanza excepciones. */
@Component
@RequiredArgsConstructor
public class EstadoApiClient {

    private final RestClient apisExternasRestClient;

    /** true si la API externa responde 2xx; false si esta caida o responde con error. */
    public boolean disponible() {
        try {
            apisExternasRestClient.get().uri("/api/estado").retrieve().toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }
}
