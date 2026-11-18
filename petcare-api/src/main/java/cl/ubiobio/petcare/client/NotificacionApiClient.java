package cl.ubiobio.petcare.client;

import cl.ubiobio.petcare.dto.RespuestaEnvioDto;
import cl.ubiobio.petcare.exception.ApiExternaException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Cliente de la API externa de notificaciones (apis-externas, Node).
 *   POST {base-url}/api/notificaciones/email   body: { para, asunto, mensaje }
 *   POST {base-url}/api/notificaciones/sms     body: { para, mensaje }
 */
@Component
@RequiredArgsConstructor
public class NotificacionApiClient {

    private final RestClient apisExternasRestClient;

    public RespuestaEnvioDto enviarEmail(String para, String asunto, String mensaje) {
        return enviar("/api/notificaciones/email", Map.of("para", para, "asunto", asunto, "mensaje", mensaje));
    }

    public RespuestaEnvioDto enviarSms(String para, String mensaje) {
        return enviar("/api/notificaciones/sms", Map.of("para", para, "mensaje", mensaje));
    }

    private RespuestaEnvioDto enviar(String uri, Map<String, String> body) {
        try {
            return apisExternasRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(RespuestaEnvioDto.class);
        } catch (RestClientException e) {
            throw new ApiExternaException("No se pudo enviar la notificacion: " + e.getMessage(), e);
        }
    }
}
