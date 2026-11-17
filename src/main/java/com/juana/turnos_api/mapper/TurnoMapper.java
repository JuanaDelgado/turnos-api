package com.juana.turnos_api.mapper;

import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.model.Turno;
import org.springframework.stereotype.Component;

@Component
public class TurnoMapper {

    public TurnoResponse toResponse(Turno turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getCliente().getId(),
                turno.getCliente().getNombre(),
                turno.getProfesional().getId(),
                turno.getProfesional().getNombre(),
                turno.getProfesional().getEspecialidad(),
                turno.getFechaHora(),
                turno.getEstado()
        );
    }
}
