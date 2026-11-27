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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnoService {

    private static final int MAXIMO_TURNOS_PENDIENTES = 3;
    private static final int HORA_APERTURA = 9;
    private static final int HORA_CIERRE = 18;

    private final TurnoRepository turnoRepository;
    private final ClienteService clienteService;
    private final ProfesionalService profesionalService;
    private final TurnoMapper turnoMapper;

    public List<TurnoResponse> listar(Long profesionalId, EstadoTurno estado) {
        log.debug("Listando turnos con profesionalId={} y estado={}", profesionalId, estado);
        List<Turno> turnos;
        if (profesionalId != null && estado != null) {
            turnos = turnoRepository.findByProfesionalIdAndEstado(profesionalId, estado);
        } else if (profesionalId != null) {
            turnos = turnoRepository.findByProfesionalId(profesionalId);
        } else if (estado != null) {
            turnos = turnoRepository.findByEstado(estado);
        } else {
            turnos = turnoRepository.findAll();
        }
        return turnos.stream().map(turnoMapper::toResponse).toList();
    }

    public Page<TurnoResponse> listarPaginado(Pageable pageable) {
        return turnoRepository.findAll(pageable).map(turnoMapper::toResponse);
    }

    public List<TurnoResponse> buscarAgenda(Long profesionalId, LocalDateTime desde, LocalDateTime hasta) {
        return turnoRepository.buscarAgendaDeProfesional(profesionalId, desde, hasta)
                .stream().map(turnoMapper::toResponse).toList();
    }

    public TurnoResponse buscarPorId(Long id) {
        return turnoMapper.toResponse(obtenerTurno(id));
    }

    @Transactional
    public TurnoResponse crear (TurnoRequest request) {
        Cliente cliente = clienteService.buscarPorId(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + request.clienteId()));
        Profesional profesional = profesionalService.buscarPorId(request.profesionalId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el profeisonal con id " + request.profesionalId()));

        validarFecha(request.fechaHora());
        long pendientes = turnoRepository.countByClienteIdAndEstado(cliente.getId(), EstadoTurno.PENDIENTE);
        if (pendientes >= MAXIMO_TURNOS_PENDIENTES) {
            log.warn("Turno rechazado: {} ya tiene {} turnos pendientes", cliente.getNombre(), pendientes);
            throw new ReglaNegocioException("El cliente ya tiene " + MAXIMO_TURNOS_PENDIENTES + " turnos pendientes");
        }

        boolean ocupado = turnoRepository.existsByProfesionalIdAndFechaHoraAndEstadoNot(
                profesional.getId(), request.fechaHora(), EstadoTurno.CANCELADO);
        if (ocupado) {
            log.warn("Turno rechazado: {} ya tiene un turno el {}", profesional.getNombre(), request.fechaHora());
            throw new HorarioOcupadoException(profesional.getNombre(), request.fechaHora());
        }

        Turno turno = new Turno(null, cliente, profesional, request.fechaHora(), EstadoTurno.PENDIENTE);
        Turno guardado = turnoRepository.save(turno);
        log.info("Turno {} creado: {} con {} el {}", guardado.getId(), cliente.getNombre(),
                profesional.getNombre(), guardado.getFechaHora());
        return turnoMapper.toResponse(guardado);
    }

    @Transactional
    public TurnoResponse cancelar(Long id) {
        Turno turno = obtenerTurno(id);
        if (turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new ReglaNegocioException("El turno " + id + " ya esta cancelado");
        }
        if (turno.getEstado() == EstadoTurno.COMPLETADO) {
            throw new ReglaNegocioException("No se puede cancelar un turno completado");
        }
        turno.setEstado(EstadoTurno.CANCELADO);
        log.info("Turno {} cancelado", id);
        return turnoMapper.toResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponse confirmar(Long id) {
        Turno turno = obtenerTurno(id);
        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new ReglaNegocioException("Solo se puede confirmar un turno pendiente. Estado actual: " + turno.getEstado());
        }
        turno.setEstado(EstadoTurno.CONFIRMADO);
        log.info("Turno {} confirmado", id);
        return turnoMapper.toResponse(turnoRepository.save(turno));
    }

    private void validarFecha(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            log.warn("Turno rechazado: fecha en el pasado ({})", fechaHora);
            throw new ReglaNegocioException("No se puede reservar un turno en el pasado");
        }
        int hora = fechaHora.getHour();
        if (hora < HORA_APERTURA || hora >= HORA_CIERRE) {
            log.warn("Turno rechazado: fuera de horario ({})", fechaHora);
            throw new ReglaNegocioException("El horario de atención es de " + HORA_APERTURA + " a " + HORA_CIERRE + " hs");
        }
    }

    private Turno obtenerTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el turno con id " + id));
    }

    private void validarLimiteDePendientes(Cliente cliente) {
        long pendientes = turnoRepository.countByClienteIdAndEstado(cliente.getId(), EstadoTurno.PENDIENTE);
        if (pendientes >= MAXIMO_TURNOS_PENDIENTES) {
            log.warn("Turno rechazado: {} ya tiene {} turnos pendientes", cliente.getNombre(), pendientes);
            throw new ReglaNegocioException("El cliente ya tiene " + MAXIMO_TURNOS_PENDIENTES + " turnos pendientes");
        }
    }
}
