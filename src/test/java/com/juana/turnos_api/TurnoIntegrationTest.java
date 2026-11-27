package com.juana.turnos_api;

import com.juana.turnos_api.dto.TurnoRequest;
import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.exception.HorarioOcupadoException;
import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.repository.ClienteRepository;
import com.juana.turnos_api.repository.ProfesionalRepository;
import com.juana.turnos_api.service.TurnoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class TurnoIntegrationTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2030, 12, 15, 10, 0);

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Test
    void deberiaCrearUnTurnoYGuardarloEnLaBase() {
        Cliente cliente = clienteRepository.save(new Cliente(null, "Ana Test", "ana.test@gmail.com", "1100000000"));
        Profesional profesional = profesionalRepository.save(new Profesional(null, "Pedro Test", "Clínico"));

        TurnoResponse creado = turnoService.crear(new TurnoRequest(cliente.getId(), profesional.getId(), FECHA));
        TurnoResponse buscado = turnoService.buscarPorId(creado.id());

        assertNotNull(creado.id());
        assertEquals(EstadoTurno.PENDIENTE, buscado.estado());
        assertEquals("Ana Test", buscado.clienteNombre());
    }

    @Test
    void deberiaRechazarUnSegundoTurnoEnElMismoHorario() {
        Cliente cliente = clienteRepository.save(new Cliente(null, "Ana Test", "ana.test@gmail.com", "1100000000"));
        Profesional profesional = profesionalRepository.save(new Profesional(null, "Pedro Test", "Clínico"));
        TurnoRequest request = new TurnoRequest(cliente.getId(), profesional.getId(), FECHA);

        turnoService.crear(request);

        assertThrows(HorarioOcupadoException.class, () -> turnoService.crear(request));
    }
}
