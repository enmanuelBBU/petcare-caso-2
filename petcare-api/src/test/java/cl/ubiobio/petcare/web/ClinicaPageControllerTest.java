package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.ClinicaResponse;
import cl.ubiobio.petcare.dto.UbicacionClinicaResponse;
import cl.ubiobio.petcare.exception.ApiExternaException;
import cl.ubiobio.petcare.service.ClinicaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClinicaPageController.class)
class ClinicaPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean ClinicaService clinicaService;

    private static final ClinicaResponse CLINICA = new ClinicaResponse(1L, "PetCare Concepcion", "Av. Collao 1202",
            "Concepcion", "Concepcion", "+5641", "contacto@petcare.cl", new BigDecimal("-36.8"), new BigDecimal("-73.0"),
            LocalTime.of(9, 0), LocalTime.of(19, 0));

    @Test
    void clinicaConMapa() throws Exception {
        when(clinicaService.listar()).thenReturn(List.of(CLINICA));
        when(clinicaService.obtenerUbicacion(1L)).thenReturn(new UbicacionClinicaResponse(1L, "PetCare Concepcion",
                "Av. Collao 1202", "Concepcion", "Concepcion", new BigDecimal("-36.8"), new BigDecimal("-73.0"),
                "https://www.openstreetmap.org/?mlat=-36.8&mlon=-73.0",
                "https://www.openstreetmap.org/export/embed.html?bbox=1,2,3,4&marker=-36.8,-73.0"));
        mvc.perform(get("/clinica"))
                .andExpect(status().isOk())
                .andExpect(view().name("clinica"))
                .andExpect(model().attribute("seccion", "clinica"))
                .andExpect(content().string(containsString("openstreetmap.org/export/embed.html")))
                .andExpect(content().string(containsString("Abrir en OpenStreetMap")));
    }

    @Test
    void clinicaSinApi() throws Exception {
        when(clinicaService.listar()).thenReturn(List.of(CLINICA));
        when(clinicaService.obtenerUbicacion(1L)).thenThrow(
                new ApiExternaException("No se pudo consultar la API de geolocalizacion", null));
        mvc.perform(get("/clinica"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("mapaError"))
                .andExpect(content().string(not(containsString("<iframe"))))
                .andExpect(content().string(containsString("PetCare Concepcion")));
    }
}
