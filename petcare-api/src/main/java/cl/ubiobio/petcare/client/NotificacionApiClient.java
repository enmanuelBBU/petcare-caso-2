package cl.ubiobio.petcare.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Cliente de la API externa de notificaciones (apis-externas, Node).
 *   POST {base-url}/api/notificaciones/email   body: { para, asunto, mensaje }
 *   POST {base-url}/api/notificaciones/sms     body: { para, mensaje }
 */
@Component
@RequiredArgsConstructor
public class NotificacionApiClient {

    private final RestClient apisExternasRestClient;

    // TODO: enviarEmail(String para, String asunto, String mensaje) -> RespuestaEnvioDto
    // TODO: enviarSms(String para, String mensaje) -> RespuestaEnvioDto
}
