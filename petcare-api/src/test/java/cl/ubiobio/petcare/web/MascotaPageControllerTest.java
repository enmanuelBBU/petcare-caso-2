package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.AtencionResponse;
import cl.ubiobio.petcare.dto.MascotaResponse;
import cl.ubiobio.petcare.dto.MedicamentoPrescritoResponse;
import cl.ubiobio.petcare.dto.TratamientoResponse;
import cl.ubiobio.petcare.model.enums.Sexo;
import cl.ubiobio.petcare.service.AtencionService;
import cl.ubiobio.petcare.service.ClienteService;
import cl.ubiobio.petcare.service.MascotaService;
import cl.ubiobio.petcare.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MascotaPageController.class)
class MascotaPageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean MascotaService mascotaService;
    @MockitoBean ClienteService clienteService;
    @MockitoBean AtencionService atencionService;
    @MockitoBean ReservaService reservaService;

    private static MascotaResponse mascota(long id, long clienteId) {
        return new MascotaResponse(id, clienteId, "Ana Rojas", "Toby", "Perro", "Mestizo", Sexo.MACHO,
                LocalDate.of(2023, 5, 10), new BigDecimal("11.5"), "Café", null, true);
    }

    @Test
    void nuevaConClientePreseleccionado() throws Exception {
        when(clienteService.listar()).thenReturn(List.of());
        mvc.perform(get("/mascotas/nueva").param("clienteId", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("mascota-form"))
                .andExpect(model().attribute("form", hasProperty("clienteId", is(4L))))
                .andExpect(model().attributeExists("clientes"))
                .andExpect(model().attribute("seccion", "mascotas"));
    }

    @Test
    void crearRedirigeAlCliente() throws Exception {
        when(mascotaService.crear(any())).thenReturn(mascota(9, 4));
        mvc.perform(post("/mascotas")
                        .param("clienteId", "4").param("nombre", "Toby").param("especie", "Perro")
                        .param("fechaNacimiento", "2023-05-10").param("peso", "11.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/4"))
                .andExpect(flash().attributeExists("ok"));
    }

    @Test
    void fechaFuturaReRender() throws Exception {
        when(clienteService.listar()).thenReturn(List.of());
        mvc.perform(post("/mascotas")
                        .param("clienteId", "4").param("nombre", "Toby").param("especie", "Perro")
                        .param("fechaNacimiento", "2030-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("mascota-form"))
                .andExpect(model().attributeHasFieldErrors("form", "fechaNacimiento"));
        verify(mascotaService, never()).crear(any());
    }

    @Test
    void detalleConHistorial() throws Exception {
        when(mascotaService.buscarPorId(2L)).thenReturn(mascota(2, 4));
        TratamientoResponse t = new TratamientoResponse(1L, 5L, "Antibiótico", "Con comida", LocalDate.now(), null,
                List.of(new MedicamentoPrescritoResponse(1L, "MED-002", "Amoxicilina 250 mg", "Amoxicilina",
                        "1 comp", "cada 12 h", 7, "Oral")));
        when(atencionService.historialPorMascota(2L)).thenReturn(List.of(new AtencionResponse(5L, 1L, 2L, "Toby",
                1L, "Carla Perez", LocalDateTime.now(), "Control", null, "Otitis leve", new BigDecimal("11.4"),
                new BigDecimal("38.5"), null, List.of(t))));
        when(reservaService.listarPorMascota(2L)).thenReturn(List.of());
        mvc.perform(get("/mascotas/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("mascota-detalle"))
                .andExpect(model().attributeExists("mascota", "historial", "reservas"))
                .andExpect(content().string(containsString("Otitis leve")))
                .andExpect(content().string(containsString("Amoxicilina 250 mg")));
    }

    @Test
    void eliminarRedirigeAlDueno() throws Exception {
        when(mascotaService.buscarPorId(2L)).thenReturn(mascota(2, 4));
        mvc.perform(post("/mascotas/2/eliminar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/4"));
        verify(mascotaService).eliminar(2L);
    }
}
