package com.juana.turnos_api.service;

import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import com.juana.turnos_api.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteService clienteService;
    private final ProfesionalService profesionalService;

    public List<Turno> listar() {
        return turnoRepository.findAll();
    }

    public Optional<Turno> buscarPorId(Long id) {
        return turnoRepository.findById(id);
    }

    public Optional<Turno> crear (Turno turno) {
        Optional<Cliente> cliente = clienteService.buscarPorId(turno.getCliente().getId());
        Optional<Profesional> profesional = profesionalService.buscarPorId(turno.getProfesional().getId());

        if (cliente.isEmpty() || profesional.isEmpty()) {
            return Optional.empty();
        }

        turno.setCliente(cliente.get());
        turno.setProfesional(profesional.get());
        return Optional.of(turnoRepository.save(turno));
    }

    public Optional<Turno> actualizar (Long id, Turno datosNuevos) {
        Optional<Turno> encontrado = buscarPorId(id);
        if (encontrado.isPresent()) {
           Turno turno = encontrado.get();
            turno.setFecha_hora(datosNuevos.getFecha_hora());
            turno.setEstado(datosNuevos.getEstado());
           turnoRepository.save(turno);
        }
        return encontrado;
    }

    public boolean eliminar(Long id) {
        if (turnoRepository.existsById(id)) {
            turnoRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
