package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.AtencionRequest;
import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.dto.TratamientoRequest;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.exception.ReglaNegocioException;
import cl.ubiobio.petcare.model.enums.EstadoReserva;
import cl.ubiobio.petcare.service.AtencionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.service.TratamientoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtencionPageController.class)
class AtencionPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean AtencionService atencionService;
    @MockitoBean TratamientoService tratamientoService;
    @MockitoBean ReservaService reservaService;

    private static AtencionResponse atencion(long id) {
        return new AtencionResponse(id, 1L, 5L, "Toby", 1L, "Carla Perez", LocalDateTime.now(), "Control", null,
                "Otitis", new BigDecimal("11.4"), new BigDecimal("38.5"), null, List.of());
    }

    @Test
    void nuevaAtencionMuestraReserva() throws Exception {
        when(reservaService.buscarPorId(1L)).thenReturn(ReservaPageControllerTest.reserva(1, EstadoReserva.CONFIRMADA));
        mvc.perform(get("/atenciones/nueva").param("reservaId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("atencion-form"))
                .andExpect(model().attributeExists("reserva"))
                .andExpect(model().attribute("form", hasProperty("reservaId", is(1L))));
    }

    @Test
    void registrarRedirigeATratamiento() throws Exception {
        when(atencionService.registrar(any())).thenReturn(atencion(9));
        mvc.perform(post("/atenciones")
                        .param("reservaId", "1").param("diagnostico", "Otitis")
                        .param("pesoRegistrado", "11.4").param("temperatura", "38.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tratamientos/nuevo?atencionId=9"))
                .andExpect(flash().attribute("ok", containsString("ATENDIDA")));
        ArgumentCaptor<AtencionRequest> captor = ArgumentCaptor.forClass(AtencionRequest.class);
        verify(atencionService).registrar(captor.capture());
        assertEquals(1L, captor.getValue().reservaId());
        assertNull(captor.getValue().mascotaId());
    }

    @Test
    void reservaNoConfirmadaReRender() throws Exception {
        String msg = "Solo se puede atender una reserva CONFIRMADA (estado actual: PENDIENTE)";
        when(atencionService.registrar(any())).thenThrow(new ReglaNegocioException(msg));
        when(reservaService.buscarPorId(1L)).thenReturn(ReservaPageControllerTest.reserva(1, EstadoReserva.PENDIENTE));
        mvc.perform(post("/atenciones").param("reservaId", "1").param("diagnostico", "Otitis"))
                .andExpect(status().isOk())
                .andExpect(view().name("atencion-form"))
                .andExpect(model().attribute("error", msg));
    }

    @Test
    void tratamientoIgnoraFilasVacias() throws Exception {
        when(atencionService.buscarPorId(9L)).thenReturn(atencion(9));
        when(tratamientoService.crear(any())).thenReturn(
                new TratamientoResponse(3L, 9L, "X", null, LocalDate.now(), null, List.of()));
        mvc.perform(post("/tratamientos")
                        .param("atencionId", "9").param("descripcion", "X")
                        .param("fechaInicio", LocalDate.now().toString())
                        .param("medicamentos[0].codigoExterno", "MED-002")
                        .param("medicamentos[0].nombre", "Amoxicilina 250 mg")
                        .param("medicamentos[0].dosis", "1 comp")
                        .param("medicamentos[0].frecuencia", "cada 12 h")
                        .param("medicamentos[0].duracionDias", "7")
                        .param("medicamentos[1].codigoExterno", "")
                        .param("medicamentos[1].nombre", "")
                        .param("medicamentos[1].dosis", "")
                        .param("medicamentos[1].frecuencia", "")
                        .param("medicamentos[1].duracionDias", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mascotas/5"))
                .andExpect(flash().attributeExists("ok"));
        ArgumentCaptor<TratamientoRequest> captor = ArgumentCaptor.forClass(TratamientoRequest.class);
        verify(tratamientoService).crear(captor.capture());
        assertEquals(1, captor.getValue().medicamentos().size());
        assertEquals("MED-002", captor.getValue().medicamentos().get(0).codigoExterno());
    }

    @Test
    void filaSinDosisReRender() throws Exception {
        when(atencionService.buscarPorId(9L)).thenReturn(atencion(9));
        mvc.perform(post("/tratamientos")
                        .param("atencionId", "9").param("descripcion", "X")
                        .param("fechaInicio", LocalDate.now().toString())
                        .param("medicamentos[0].nombre", "Amoxicilina 250 mg")
                        .param("medicamentos[0].dosis", "")
                        .param("medicamentos[0].frecuencia", "cada 12 h"))
                .andExpect(status().isOk())
                .andExpect(view().name("tratamiento-form"))
                .andExpect(model().attributeHasFieldErrors("form", "medicamentos[0].dosis"))
                .andExpect(content().string(containsString("Amoxicilina 250 mg")));
        verify(tratamientoService, never()).crear(any());
    }

    @Test
    void nuevoTratamientoFechaHoy() throws Exception {
        when(atencionService.buscarPorId(9L)).thenReturn(atencion(9));
        mvc.perform(get("/tratamientos/nuevo").param("atencionId", "9"))
                .andExpect(status().isOk())
                .andExpect(view().name("tratamiento-form"))
                .andExpect(model().attribute("form", hasProperty("fechaInicio", is(LocalDate.now()))))
                .andExpect(content().string(containsString("/medicamentos.js")));
    }
}
