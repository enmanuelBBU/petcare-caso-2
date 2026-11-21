package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.NotificacionResponse;
import cl.ubiobio.petcare.dto.ReservaResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.model.enums.CanalNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoNotificacion;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.model.enums.TipoNotificacion;
import cl.ubiobio.petcare.service.NotificacionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.service.VeterinarioService;
import cl.ubiobio.petcare.service.MascotaService;
import cl.ubiobio.petcare.service.TipoServicioService;
import cl.ubiobio.petcare.dto.ReservaRequest;
import cl.ubiobio.petcare.exception.ReservaDuplicadaException;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaPageController.class)
class ReservaPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean ReservaService reservaService;
    @MockitoBean NotificacionService notificacionService;
    @MockitoBean VeterinarioService veterinarioService;
    @MockitoBean MascotaService mascotaService;
    @MockitoBean TipoServicioService tipoServicioService;

    // ------------------------------------------------------------------ crear / modificar (Task 5)

    @Test
    void nuevaPreseleccionaMascota() throws Exception {
        mvc.perform(get("/reservas/nueva").param("mascotaId", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-form"))
                .andExpect(model().attribute("form", hasProperty("mascotaId", is(5L))))
                .andExpect(model().attribute("modo", "crear"))
                .andExpect(model().attributeExists("mascotas", "veterinarios", "servicios"))
                .andExpect(content().string(containsString("/reserva.js")));
    }

    @Test
    void crearValida() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(3);
        when(reservaService.crear(any())).thenReturn(reserva(12, EstadoReserva.PENDIENTE));
        mvc.perform(post("/reservas")
                        .param("mascotaId", "5").param("veterinarioId", "1")
                        .param("fecha", fecha.toString()).param("hora", "10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas/12"))
                .andExpect(flash().attributeExists("ok"));
        ArgumentCaptor<ReservaRequest> captor = ArgumentCaptor.forClass(ReservaRequest.class);
        verify(reservaService).crear(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(LocalTime.of(10, 0), captor.getValue().hora());
        org.junit.jupiter.api.Assertions.assertEquals(fecha, captor.getValue().fecha());
    }

    @Test
    void crearDuplicadaReRender() throws Exception {
        String msg = "El veterinario ya tiene una reserva el 2026-11-23 a las 10:00";
        when(reservaService.crear(any())).thenThrow(new ReservaDuplicadaException(msg));
        mvc.perform(post("/reservas")
                        .param("mascotaId", "5").param("veterinarioId", "1")
                        .param("fecha", LocalDate.now().plusDays(3).toString()).param("hora", "10:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-form"))
                .andExpect(model().attribute("error", msg))
                .andExpect(model().attribute("form", hasProperty("hora", is(LocalTime.of(10, 0)))))
                .andExpect(content().string(containsString(msg)));
    }

    @Test
    void sinHoraYFechaPasadaReRender() throws Exception {
        mvc.perform(post("/reservas")
                        .param("mascotaId", "5").param("veterinarioId", "1")
                        .param("fecha", "2020-01-01").param("hora", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-form"))
                .andExpect(model().attributeHasFieldErrors("form", "hora", "fecha"))
                .andExpect(model().attribute("form", hasProperty("mascotaId", is(5L))));
        verify(reservaService, never()).crear(any());
    }

    @Test
    void fueraDeHorario() throws Exception {
        String msg = "El veterinario Carla Perez no atiende el domingo";
        when(reservaService.crear(any())).thenThrow(new ReglaNegocioException(msg));
        mvc.perform(post("/reservas")
                        .param("mascotaId", "5").param("veterinarioId", "1")
                        .param("fecha", LocalDate.now().plusDays(3).toString()).param("hora", "10:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-form"))
                .andExpect(model().attribute("error", msg));
    }

    @Test
    void editarCargaReserva() throws Exception {
        when(reservaService.buscarPorId(7L)).thenReturn(reserva(7, EstadoReserva.PENDIENTE));
        mvc.perform(get("/reservas/7/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-form"))
                .andExpect(model().attribute("modo", "editar"))
                .andExpect(model().attribute("form", hasProperty("veterinarioId", is(1L))))
                .andExpect(model().attributeExists("reserva"));
    }

    @Test
    void modificarRedirige() throws Exception {
        when(reservaService.modificar(eq(7L), any())).thenReturn(reserva(7, EstadoReserva.PENDIENTE));
        mvc.perform(post("/reservas/7")
                        .param("mascotaId", "5").param("veterinarioId", "1")
                        .param("fecha", LocalDate.now().plusDays(4).toString()).param("hora", "11:30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas/7"))
                .andExpect(flash().attributeExists("ok"));
        verify(reservaService).modificar(eq(7L), any());
    }

    // ------------------------------------------------------------------ listado / estados (Task 4)

    static ReservaResponse reserva(long id, EstadoReserva estado) {
        return new ReservaResponse(id, LocalDate.now().plusDays(3), LocalTime.of(10, 0), estado, "Control", null,
                5L, "Toby", 4L, "Ana Rojas", 1L, "Carla Perez", null, null, 1L,
                LocalDateTime.now(), LocalDateTime.now());
    }

    static NotificacionResponse notif(long reservaId, EstadoNotificacion estado) {
        return new NotificacionResponse(1L, 4L, reservaId, CanalNotificacion.EMAIL, TipoNotificacion.CONFIRMACION,
                "ana@mail.cl", "Reserva confirmada", "msg", estado, null, LocalDateTime.now(), null);
    }

    @Test
    void listarFiltraPorEstado() throws Exception {
        when(reservaService.listar(EstadoReserva.PENDIENTE, null, null)).thenReturn(List.of(reserva(1, EstadoReserva.PENDIENTE)));
        mvc.perform(get("/reservas").param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservas"))
                .andExpect(model().attribute("reservas", hasSize(1)))
                .andExpect(model().attributeExists("veterinarios", "estados"))
                .andExpect(model().attribute("seccion", "reservas"));
        verify(reservaService).listar(EstadoReserva.PENDIENTE, null, null);
    }

    @Test
    void detalleConHistorial() throws Exception {
        when(reservaService.buscarPorId(1L)).thenReturn(reserva(1, EstadoReserva.CONFIRMADA));
        when(reservaService.historialEstados(1L)).thenReturn(List.of());
        mvc.perform(get("/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("reserva-detalle"))
                .andExpect(model().attributeExists("reserva", "historial"))
                .andExpect(content().string(containsString("badge-CONFIRMADA")))
                .andExpect(content().string(containsString("Registrar atención")));
    }

    @Test
    void confirmarNotificaEnviada() throws Exception {
        when(reservaService.confirmar(eq1(), isNull())).thenReturn(reserva(1, EstadoReserva.CONFIRMADA));
        when(notificacionService.listarPorCliente(4L)).thenReturn(List.of(notif(1, EstadoNotificacion.ENVIADA)));
        mvc.perform(post("/reservas/1/confirmar").param("motivo", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas/1"))
                .andExpect(flash().attribute("ok", containsString("Notificación enviada")));
    }

    @Test
    void confirmarNotificaFallida() throws Exception {
        when(reservaService.confirmar(eq1(), any())).thenReturn(reserva(1, EstadoReserva.CONFIRMADA));
        when(notificacionService.listarPorCliente(4L)).thenReturn(List.of(
                notif(2, EstadoNotificacion.ENVIADA), notif(1, EstadoNotificacion.FALLIDA)));
        mvc.perform(post("/reservas/1/confirmar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", containsString("la notificación falló")));
    }

    @Test
    void cancelarConMotivo() throws Exception {
        when(reservaService.cancelar(1L, "Cliente no puede")).thenReturn(reserva(1, EstadoReserva.CANCELADA));
        when(notificacionService.listarPorCliente(4L)).thenReturn(List.of(notif(1, EstadoNotificacion.ENVIADA)));
        mvc.perform(post("/reservas/1/cancelar").param("motivo", "Cliente no puede"))
                .andExpect(redirectedUrl("/reservas/1"))
                .andExpect(flash().attribute("ok", org.hamcrest.Matchers.startsWith("Reserva cancelada")));
    }

    @Test
    void transicionInvalida() throws Exception {
        String msg = "No se puede pasar una reserva de CONFIRMADA a CONFIRMADA";
        when(reservaService.confirmar(eq1(), any())).thenThrow(new ReglaNegocioException(msg));
        mvc.perform(post("/reservas/1/confirmar").header("Referer", "/reservas/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas/1"))
                .andExpect(flash().attribute("error", msg));
    }

    @Test
    void detallePendienteNoMuestraAtender() throws Exception {
        when(reservaService.buscarPorId(1L)).thenReturn(reserva(1, EstadoReserva.PENDIENTE));
        when(reservaService.historialEstados(1L)).thenReturn(List.of());
        mvc.perform(get("/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Registrar atención"))))
                .andExpect(content().string(containsString("Confirmar")));
    }

    private static Long eq1() {
        return org.mockito.ArgumentMatchers.eq(1L);
    }
}
