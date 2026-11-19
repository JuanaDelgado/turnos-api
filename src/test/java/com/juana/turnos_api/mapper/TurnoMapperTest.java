package com.juana.turnos_api.mapper;

import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TurnoMapperTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 12, 15, 10, 0);

    private TurnoMapper turnoMapper;
    private Turno turno;

    @BeforeEach
    void setUp() {
        turnoMapper = new TurnoMapper();
        Cliente cliente = new Cliente(1L, "Noelia Martínez", "noelia@gmail,com", "1112345678");
        Profesional profesional = new Profesional(2L, "Antonia Blanco", "Dermatóloga");
        turno = new Turno(10L, cliente, profesional, FECHA, EstadoTurno.PENDIENTE);
    }

    @Test
    void deberiaDevolverUnaRespuesta() {
        TurnoResponse response = turnoMapper.toResponse(turno);

        assertNotNull(response);
    }

    @Test
    void deberiaCopiarLosDatosDelTurno() {
        TurnoResponse response = turnoMapper.toResponse(turno);

        assertEquals(10L, response.id());
        assertEquals(FECHA, response.fechaHora());
        assertEquals(EstadoTurno.PENDIENTE, response.estado());
    }

    @Test
    void deberiaCopiarLosDatosDelCliente() {
        TurnoResponse response = turnoMapper.toResponse(turno);

        assertEquals(1L, response.clienteId());
        assertEquals("Noelia Martínez", response.clienteNombre());
    }

    @Test
    void deberiaCopiarLosDatosDelProfesional() {
        TurnoResponse response = turnoMapper.toResponse(turno);

        assertEquals(2L, response.profesionalId());
        assertEquals("Antonia Blanco", response.profesionalNombre());
        assertEquals("Dermatóloga", response.especialidad());
    }

    @Test
    void deberiaFallarSiElTurnoNoTieneCliente() {
        turno.setCliente(null);

        assertThrows(NullPointerException.class, () -> turnoMapper.toResponse(turno));
    }
}
