package com.juana.turnos_api.controller;

import com.juana.turnos_api.dto.TurnoRequest;
import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.exception.HorarioOcupadoException;
import com.juana.turnos_api.exception.RecursoNoEncontradoException;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.service.TurnoService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TurnoController.class)
public class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    private TurnoResponse turnoDeEjemplo() {
        return new TurnoResponse(10L, 1L, "Noelia Martínez", 2L, "Antonia Blanco",
                "Dermatóloga", LocalDateTime.of(2030, 12, 15, 10, 0), EstadoTurno.PENDIENTE);
    }

    @Test
    void deberiaDevolverUnTurnoPorId() throws Exception {
        when(turnoService.buscarPorId(10L)).thenReturn(turnoDeEjemplo());

        mockMvc.perform(get("/turnos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.clienteNombre").value("Noelia Martínez"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void deberiaDevolver404SiElTurnoNoExiste() throws Exception {
        when(turnoService.buscarPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe el turno con id 99"));

        mockMvc.perform(get("/turnos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el turno con id 99"));
    }

    @Test
    void deberiaCrearUnTurno() throws Exception {
        when(turnoService.crear(any(TurnoRequest.class))).thenReturn(turnoDeEjemplo());
        String json = """
                {
                  "clienteId": 1,
                  "profesionalId": 2,
                  "fechaHora": "2030-12-15T10:00:00"
                }
                """;

        mockMvc.perform(post("/turnos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deberiaDevolver400SiFaltaElProfesional() throws Exception {
        String json = """
                {
                  "clienteId": 1,
                  "fechaHora": "2030-12-15T10:00:00"
                }
                """;

        mockMvc.perform(post("/turnos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.profesionalId").value("El profesional es obligatorio"));
    }

    @Test
    void deberiaDevolver409SiElHorarioEstaOcupado() throws Exception {
        when(turnoService.crear(any(TurnoRequest.class)))
                .thenThrow(new HorarioOcupadoException("Antonia Blanco", LocalDateTime.of(2030, 12, 15, 10, 0)));
        String json = """
                {
                  "clienteId": 1,
                  "profesionalId": 2,
                  "fechaHora": "2030-12-15T10:00:00"
                }
                """;

        mockMvc.perform(post("/turnos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void deberiaDevolver400SiElEstadoEsInvalido() throws Exception {
        mockMvc.perform(get("/turnos").param("estado", "HOLA"))
                .andExpect(status().isBadRequest());
    }
}
