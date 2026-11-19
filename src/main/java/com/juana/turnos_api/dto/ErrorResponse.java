package com.juana.turnos_api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String mensaje,
        Map<String, String> errores
) {
}
