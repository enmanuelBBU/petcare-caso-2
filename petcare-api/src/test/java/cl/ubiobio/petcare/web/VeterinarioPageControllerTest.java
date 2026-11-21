package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.HorarioResponse;
import cl.ubiobio.petcare.dto.VeterinarioResponse;
import cl.ubiobio.petcare.service.VeterinarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VeterinarioPageController.class)
class VeterinarioPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean VeterinarioService veterinarioService;

    @Test
    void listaConHorarios() throws Exception {
        when(veterinarioService.listar(null, false)).thenReturn(List.of(new VeterinarioResponse(1L, "11111111-1",
                "Carla", "Perez", "carla@petcare.cl", null, "Medicina general", 1L, "PetCare", true)));
        when(veterinarioService.listarHorarios(1L)).thenReturn(List.of(
                new HorarioResponse(1L, 1, "lunes", LocalTime.of(9, 0), LocalTime.of(13, 0))));
        mvc.perform(get("/veterinarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("veterinarios"))
                .andExpect(model().attribute("seccion", "veterinarios"))
                .andExpect(model().attributeExists("veterinarios", "horarios"))
                .andExpect(content().string(containsString("09:00")))
                .andExpect(content().string(containsString("Carla Perez")));
    }
}
