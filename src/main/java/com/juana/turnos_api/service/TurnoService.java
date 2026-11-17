package com.juana.turnos_api.service;

import com.juana.turnos_api.dto.TurnoRequest;
import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.exception.HorarioOcupadoException;
import com.juana.turnos_api.exception.RecursoNoEncontradoException;
import com.juana.turnos_api.mapper.TurnoMapper;
import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import com.juana.turnos_api.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteService clienteService;
    private final ProfesionalService profesionalService;
    private final TurnoMapper turnoMapper;

    public List<TurnoResponse> listar(Long profesionalId, EstadoTurno estado) {
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

    public TurnoResponse crear (TurnoRequest request) {
        Cliente cliente = clienteService.buscarPorId(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + request.clienteId()));
        Profesional profesional = profesionalService.buscarPorId(request.profesionalId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el profeisonal con id " + request.profesionalId()));

        boolean ocupado = turnoRepository.existsByProfesionalIdAndFechaHora(
                profesional.getId(), request.fechaHora());
        if (ocupado) {
            throw new HorarioOcupadoException(profesional.getNombre(), request.fechaHora());
        }

        Turno turno = new Turno(null, cliente, profesional, request.fechaHora(), EstadoTurno.PENDIENTE);
        return turnoMapper.toResponse(turnoRepository.save(turno));
    }

    private Turno obtenerTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el turno con id " + id));
    }

}
