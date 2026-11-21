package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.model.enums.CanalNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.TipoNotificacion;
import cl.ubiobio.petcare.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionPageController.class)
class NotificacionPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean NotificacionService notificacionService;

    private static NotificacionResponse notif(long id, EstadoNotificacion estado) {
        return new NotificacionResponse(id, 4L, 1L, CanalNotificacion.EMAIL, TipoNotificacion.CONFIRMACION,
                "ana@mail.cl", "Reserva confirmada", "msg", estado, null, LocalDateTime.now(), null);
    }

    @Test
    void filtraFallidas() throws Exception {
        when(notificacionService.listar(EstadoNotificacion.FALLIDA)).thenReturn(List.of(notif(3, EstadoNotificacion.FALLIDA)));
        mvc.perform(get("/notificaciones").param("estado", "FALLIDA"))
                .andExpect(status().isOk())
                .andExpect(view().name("notificaciones"))
                .andExpect(model().attribute("seccion", "notificaciones"))
                .andExpect(model().attribute("notificaciones", hasSize(1)))
                .andExpect(content().string(containsString("Reenviar")));
        verify(notificacionService).listar(EstadoNotificacion.FALLIDA);
    }

    @Test
    void enviadaNoMuestraReenviar() throws Exception {
        when(notificacionService.listar(null)).thenReturn(List.of(notif(3, EstadoNotificacion.ENVIADA)));
        mvc.perform(get("/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("/notificaciones/3/reenviar"))));
    }

    @Test
    void reenviar() throws Exception {
        when(notificacionService.reenviar(3L)).thenReturn(notif(3, EstadoNotificacion.ENVIADA));
        mvc.perform(post("/notificaciones/3/reenviar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notificaciones"))
                .andExpect(flash().attribute("ok", containsString("ana@mail.cl")));
    }

    @Test
    void reenviarFallaDeNuevo() throws Exception {
        when(notificacionService.reenviar(3L)).thenReturn(notif(3, EstadoNotificacion.FALLIDA));
        mvc.perform(post("/notificaciones/3/reenviar"))
                .andExpect(redirectedUrl("/notificaciones"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void recordatorios() throws Exception {
        when(notificacionService.enviarRecordatorios()).thenReturn(List.of(
                notif(5, EstadoNotificacion.ENVIADA), notif(6, EstadoNotificacion.ENVIADA)));
        mvc.perform(post("/notificaciones/recordatorios"))
                .andExpect(redirectedUrl("/notificaciones"))
                .andExpect(flash().attribute("ok", containsString("2")));
    }
}
