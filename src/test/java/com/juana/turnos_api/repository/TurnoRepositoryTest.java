package com.juana.turnos_api.repository;

import com.juana.turnos_api.TestcontainersConfiguration;
import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class TurnoRepositoryTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2030, 12, 15, 10, 0);

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    private Cliente cliente;
    private Profesional profesional;

    @BeforeEach
    void setUp() {
        cliente = clienteRepository.save(new Cliente(null, "Ana Test", "ana.test@gmail.com", "1100000000"));
        profesional = profesionalRepository.save(new Profesional(null, "Pedro Test", "Clínico"));
    }

    @Test
    void deberiaDetectarUnHorarioOcupado() {
        turnoRepository.save(new Turno(null, cliente, profesional, FECHA, EstadoTurno.PENDIENTE));

        boolean ocupado = turnoRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesional.getId(), FECHA, EstadoTurno.CANCELADO);

        assertTrue(ocupado);
    }

    @Test
    void noDeberiaContarLosTurnosCancelados() {
        turnoRepository.save(new Turno(null, cliente, profesional, FECHA, EstadoTurno.CANCELADO));

        boolean ocupado = turnoRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesional.getId(), FECHA, EstadoTurno.CANCELADO);

        assertFalse(ocupado);
    }

    @Test
    void deberiaBuscarTurnosPorEstado() {
        turnoRepository.save(new Turno(null, cliente, profesional, FECHA, EstadoTurno.PENDIENTE));
        turnoRepository.save(new Turno(null, cliente, profesional, FECHA.plusHours(1), EstadoTurno.CONFIRMADO));

        List<Turno> pendientes = turnoRepository.findByEstado(EstadoTurno.PENDIENTE);

        assertEquals(1, pendientes.size());
    }
}
