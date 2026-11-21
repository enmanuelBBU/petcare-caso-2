package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.client.EstadoApiClient;
import cl.ubiobio.petcare.dto.ResumenResponse;
import cl.ubiobio.petcare.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InicioPageController.class)
class InicioPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean ReporteService reporteService;
    @MockitoBean EstadoApiClient estadoApiClient;

    @BeforeEach
    void stubs() {
        when(reporteService.agenda(any())).thenReturn(List.of());
        when(reporteService.resumen(any(), any())).thenReturn(
                new ResumenResponse(LocalDate.now().withDayOfMonth(1), LocalDate.now(), 0, Map.of(), 0, 0, BigDecimal.ZERO));
    }

    @Test
    void inicioMuestraAgendaResumenYEstadoApis() throws Exception {
        when(estadoApiClient.disponible()).thenReturn(true);
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attribute("seccion", "inicio"))
                .andExpect(model().attributeExists("agenda", "resumen"))
                .andExpect(model().attribute("apisOk", true))
                .andExpect(content().string(containsString("APIs externas")));
    }

    @Test
    void inicioConApiCaidaMuestraAviso() throws Exception {
        when(estadoApiClient.disponible()).thenReturn(false);
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("apisOk", false))
                .andExpect(content().string(containsString("sin conexión")));
    }
}
