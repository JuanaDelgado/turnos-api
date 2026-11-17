package com.juana.turnos_api.exception;

import java.time.LocalDateTime;

public class HorarioOcupadoException extends RuntimeException {

    public HorarioOcupadoException(String profesional, LocalDateTime fechaHora) {
        super("El profesional " + profesional + " ya tiene un turno el " + fechaHora);
    }
}
