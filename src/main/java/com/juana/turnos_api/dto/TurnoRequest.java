package com.juana.turnos_api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TurnoRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El profesional es obligatorio")
        Long profesionalId,

        @NotNull(message = "La fecha y hora es obligatoria")
        @Future(message = "La fecha del turno tiene que ser futura")
        LocalDateTime fechaHora
) {

}
