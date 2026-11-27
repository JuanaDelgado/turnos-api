package com.juana.turnos_api.service;

import com.juana.turnos_api.dto.TurnoRequest;
import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.exception.HorarioOcupadoException;
import com.juana.turnos_api.exception.RecursoNoEncontradoException;
import com.juana.turnos_api.exception.ReglaNegocioException;
import com.juana.turnos_api.mapper.TurnoMapper;
import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import com.juana.turnos_api.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProfesionalService profesionalService;

    @Spy
    private TurnoMapper turnoMapper = new TurnoMapper();

    @InjectMocks
    private TurnoService turnoService;

    private Cliente cliente;
    private Profesional profesional;
    private LocalDateTime fechaValida;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Noelia Martínez", "noelia@gmail.com", "1112345678");
        profesional = new Profesional(2L, "Antonia Blanco", "Dermatóloga");
        fechaValida = LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void deberiaCrearTurnoCuandoTodoEsValido() {
        TurnoRequest request = new TurnoRequest(1L, 2L, fechaValida);
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(profesionalService.buscarPorId(2L)).thenReturn(Optional.of(profesional));
        when(turnoRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(2L, fechaValida, EstadoTurno.CANCELADO))
                .thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(invocacion -> {
            Turno turnoGuardado = invocacion.getArgument(0);
            turnoGuardado.setId(100L);
            return turnoGuardado;
        });

        TurnoResponse response = turnoService.crear(request);

        assertEquals(100L, response.id());
        assertEquals(EstadoTurno.PENDIENTE, response.estado());
        assertEquals("Noelia Martínez", response.clienteNombre());
        verify(turnoRepository).save(any(Turno.class));
    }

    @Test
    void deberiaRechazarTurnoSiElHorarioEstaOcupado() {
        TurnoRequest request = new TurnoRequest(1L, 2L, fechaValida);
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(profesionalService.buscarPorId(2L)).thenReturn(Optional.of(profesional));
        when(turnoRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(2L, fechaValida, EstadoTurno.CANCELADO))
                .thenReturn(true);

        assertThrows(HorarioOcupadoException.class, () -> turnoService.crear(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void deberiaRechazarTurnoEnElPasado() {
        LocalDateTime ayer = LocalDateTime.now().minusDays(1).withHour(10);
        TurnoRequest request = new TurnoRequest(1L, 2L, ayer);
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(profesionalService.buscarPorId(2L)).thenReturn(Optional.of(profesional));

        assertThrows(ReglaNegocioException.class, () -> turnoService.crear(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }

    @Test
    void deberiaRechazarTurnoFueraDeHorario() {
        TurnoRequest request = new TurnoRequest(1L, 2L, fechaValida.withHour(20));
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(profesionalService.buscarPorId(2L)).thenReturn(Optional.of(profesional));

        assertThrows(ReglaNegocioException.class, () -> turnoService.crear(request));
    }

    @Test
    void deberiaLanzarErrorSiElClienteNoExiste() {
        TurnoRequest request = new TurnoRequest(1L, 2L, fechaValida);
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> turnoService.crear(request));
    }

    @Test
    void deberiaRechazarCancelarUnTurnoYaCancelado() {
        Turno turno = new Turno(5L, cliente, profesional, fechaValida, EstadoTurno.CANCELADO);
        when(turnoRepository.findById(5L)).thenReturn(Optional.of(turno));

        assertThrows(ReglaNegocioException.class, () -> turnoService.cancelar(5L));
    }

    @Test
    void deberiaConfirmarUnTurnoPendiente() {
        Turno turno = new Turno(5L, cliente, profesional, fechaValida, EstadoTurno.PENDIENTE);
        when(turnoRepository.findById(5L)).thenReturn(Optional.of(turno));
        when(turnoRepository.save(any(Turno.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        TurnoResponse response = turnoService.confirmar(5L);

        assertEquals(EstadoTurno.CONFIRMADO, response.estado());
    }

    @Test
    void deberiaRechazarConfirmarUnTurnoCancelado() {
        Turno turno = new Turno(5L, cliente, profesional, fechaValida, EstadoTurno.CANCELADO);
        when(turnoRepository.findById(5L)).thenReturn(Optional.of(turno));

        assertThrows(ReglaNegocioException.class, () -> turnoService.confirmar(5L));
    }

    @Test
    void deberiaRechazarTurnoSiElClienteYaTieneTresPendientes() {
        TurnoRequest request = new TurnoRequest(1L, 2L, fechaValida);
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(profesionalService.buscarPorId(2L)).thenReturn(Optional.of(profesional));
        when(turnoRepository.countByClienteIdAndEstado(1L, EstadoTurno.PENDIENTE)).thenReturn(3L);

        assertThrows(ReglaNegocioException.class, () -> turnoService.crear(request));
        verify(turnoRepository, never()).save(any(Turno.class));
    }
}
