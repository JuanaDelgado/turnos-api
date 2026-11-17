package com.juana.turnos_api.dto;

import com.juana.turnos_api.model.EstadoTurno;

import java.time.LocalDateTime;

public record TurnoResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        Long profesionalId,
        String profesionalNombre,
        String especialidad,
        LocalDateTime fechaHora,
        EstadoTurno estado
) {
}
