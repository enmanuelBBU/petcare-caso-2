package cl.ubiobio.petcare.dto;

import cl.ubiobio.petcare.model.entity.Notificacion;
import cl.ubiobio.petcare.model.enums.CanalNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.TipoNotificacion;

import java.time.LocalDateTime;

/** Notificacion registrada (enviada, fallida o pendiente). */
public record NotificacionResponse(
        Long id,
        Long clienteId,
        Long reservaId,
        CanalNotificacion canal,
        TipoNotificacion tipo,
        String destinatario,
        String asunto,
        String mensaje,
        EstadoNotificacion estado,
        String respuestaApi,
        LocalDateTime creadoEn,
        LocalDateTime enviadoEn
) {
    public static NotificacionResponse de(Notificacion n) {
        return new NotificacionResponse(n.getId(), n.getCliente().getId(),
                n.getReserva() != null ? n.getReserva().getId() : null,
                n.getCanal(), n.getTipo(), n.getDestinatario(), n.getAsunto(), n.getMensaje(),
                n.getEstado(), n.getRespuestaApi(), n.getCreadoEn(), n.getEnviadoEn());
    }
}
