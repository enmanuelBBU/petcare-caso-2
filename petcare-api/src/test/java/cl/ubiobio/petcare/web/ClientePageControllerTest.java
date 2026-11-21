package cl.ubiobio.petcare.web;

import cl.ubiobio.petcare.dto.ClienteResponse;
import cl.ubiobio.petcare.exception.RegistroDuplicadoException;
import cl.ubiobio.petcare.exception.ResourceNotFoundException;
import cl.ubiobio.petcare.service.ClienteService;
import cl.ubiobio.petcare.service.NotificacionService;
import cl.ubiobio.petcare.service.ReservaService;
import cl.ubiobio.petcare.web.form.ClienteForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientePageController.class)
class ClientePageControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean ClienteService clienteService;
    @MockitoBean ReservaService reservaService;
    @MockitoBean NotificacionService notificacionService;

    private static ClienteResponse cliente(long id, String rut) {
        return new ClienteResponse(id, rut, "Ana", "Rojas", "ana@mail.cl", "+56911111111", "Calle 1", LocalDateTime.now());
    }

    @Test
    void listaClientes() throws Exception {
        when(clienteService.listar()).thenReturn(List.of(cliente(1, "12345678-9")));
        mvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes"))
                .andExpect(model().attribute("clientes", hasSize(1)))
                .andExpect(model().attribute("seccion", "clientes"));
    }

    @Test
    void buscarPorRut() throws Exception {
        when(clienteService.buscarPorRut("12345678-9")).thenReturn(cliente(1, "12345678-9"));
        mvc.perform(get("/clientes").param("rut", "12345678-9"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("clientes", hasSize(1)));
        verify(clienteService).buscarPorRut("12345678-9");
        verify(clienteService, never()).listar();
    }

    @Test
    void crearValidoRedirigeConFlash() throws Exception {
        when(clienteService.crear(any())).thenReturn(cliente(4, "20111222-3"));
        mvc.perform(post("/clientes")
                        .param("rut", "20111222-3").param("nombre", "Ana").param("apellido", "Rojas")
                        .param("email", "ana@mail.cl").param("telefono", "+56911111111"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/4"))
                .andExpect(flash().attributeExists("ok"));
    }

    @Test
    void crearInvalidoReRender() throws Exception {
        mvc.perform(post("/clientes")
                        .param("rut", "123").param("nombre", "Ana").param("apellido", "Rojas")
                        .param("email", "malo").param("telefono", "+56911111111"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-form"))
                .andExpect(model().attributeHasFieldErrors("form", "rut", "email"));
        verify(clienteService, never()).crear(any());
    }

    @Test
    void crearDuplicadoMuestraError() throws Exception {
        when(clienteService.crear(any())).thenThrow(new RegistroDuplicadoException("Ya existe un cliente con RUT 20111222-3"));
        mvc.perform(post("/clientes")
                        .param("rut", "20111222-3").param("nombre", "Ana").param("apellido", "Rojas")
                        .param("email", "ana@mail.cl").param("telefono", "+56911111111"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-form"))
                .andExpect(model().attribute("error", containsString("Ya existe")))
                .andExpect(model().attribute("form", hasProperty("rut", is("20111222-3"))))
                .andExpect(content().string(containsString("Ya existe un cliente con RUT 20111222-3")));
    }

    @Test
    void detalleMuestraMascotasReservasNotificaciones() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(cliente(1, "12345678-9"));
        when(clienteService.listarMascotas(1L)).thenReturn(List.of());
        when(reservaService.listarPorCliente(1L)).thenReturn(List.of());
        when(notificacionService.listarPorCliente(1L)).thenReturn(List.of());
        mvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-detalle"))
                .andExpect(model().attributeExists("cliente", "mascotas", "reservas", "notificaciones"));
    }

    @Test
    void detalleInexistente404() throws Exception {
        when(clienteService.buscarPorId(999L)).thenThrow(new ResourceNotFoundException("Cliente", 999L));
        mvc.perform(get("/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(content().string(containsString("Cliente con id 999 no existe")));
    }

    @Test
    void eliminarConAsociadosMuestraError() throws Exception {
        doThrow(new DataIntegrityViolationException("fk")).when(clienteService).eliminar(1L);
        mvc.perform(post("/clientes/1/eliminar").header("Referer", "/clientes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attribute("error", "No se puede eliminar: tiene registros asociados"));
    }

    @Test
    void buscarRutInexistenteMuestraListaVacia() throws Exception {
        when(clienteService.buscarPorRut("99999999-9")).thenThrow(new ResourceNotFoundException("No existe un cliente con RUT 99999999-9"));
        mvc.perform(get("/clientes").param("rut", "99999999-9"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes"))
                .andExpect(model().attribute("clientes", hasSize(0)))
                .andExpect(model().attribute("error", containsString("99999999-9")));
    }

    @Test
    void refererDeOtroSitioSoloUsaLaRuta() throws Exception {
        doThrow(new DataIntegrityViolationException("fk")).when(clienteService).eliminar(1L);
        mvc.perform(post("/clientes/1/eliminar").header("Referer", "http://otro-sitio.com/clientes?x=1"))
                .andExpect(redirectedUrl("/clientes?x=1"));
    }

    @Test
    void editarCargaDatos() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(cliente(1, "12345678-9"));
        mvc.perform(get("/clientes/1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente-form"))
                .andExpect(model().attribute("modo", "editar"))
                .andExpect(model().attribute("form", instanceOf(ClienteForm.class)))
                .andExpect(model().attribute("form", hasProperty("rut", is("12345678-9"))));
    }
}
