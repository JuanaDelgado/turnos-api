package com.juana.turnos_api.config;

import com.juana.turnos_api.model.Cliente;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.model.Turno;
import com.juana.turnos_api.repository.ClienteRepository;
import com.juana.turnos_api.repository.ProfesionalRepository;
import com.juana.turnos_api.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final TurnoRepository turnoRepository;

    @Override
    public void run(String... args) {
        if (profesionalRepository.count() > 0) {
            log.info("La base ya tiene datos: no se cargan de ejemplo.");
            return;
        }

        Cliente noelia = clienteRepository.save(new Cliente(null, "Noelia Martínez", "noelia@gmail.com", "1112345678"));
        Cliente tomas = clienteRepository.save(new Cliente(null, "Tomás López", "tomas@gmail.com", "1112348765"));

        Profesional sebastian = profesionalRepository.save(new Profesional(null, "Sebastian Brey", "Cirujano"));
        Profesional antonia = profesionalRepository.save(new Profesional(null, "Antonia Blanco", "Dermatóloga"));

        turnoRepository.save(new Turno(null, noelia, sebastian,
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0),
                EstadoTurno.PENDIENTE));
        turnoRepository.save(new Turno(null, tomas, antonia,
                LocalDateTime.now().plusDays(5).withHour(15).withMinute(30).withSecond(0).withNano(0),
                EstadoTurno.CONFIRMADO));

        log.info("Datos de ejemplo cargados.");

    }
}
