package cl.ubiobio.petcare.service;

import cl.ubiobio.petcare.repository.NotificacionRepository;
import cl.ubiobio.petcare.client.NotificacionApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Envio y registro de notificaciones a clientes. */
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionApiClient notificacionApiClient;

    // TODO: notificarConfirmacion(reserva), notificarCancelacion(reserva), notificarModificacion(reserva)
    // TODO: enviar recordatorios de reservas del dia siguiente y vacunas por vencer
    // TODO: guardar cada envio con su estado ENVIADA/FALLIDA
    // TODO: reenviar notificacion fallida
}
