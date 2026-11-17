package com.juana.turnos_api.dto;

import java.time.LocalDateTime;

public record TurnoRequest(
        Long clienteId,
        Long profesionalId,
        LocalDateTime fechaHora
) {

}
