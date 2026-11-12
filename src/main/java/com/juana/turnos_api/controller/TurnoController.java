package com.juana.turnos_api.controller;

import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Turno;
import com.juana.turnos_api.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    public List<Turno> listar(@RequestParam(required = false) Long profesionalId,
                              @RequestParam(required = false)EstadoTurno estado) {
        return turnoService.listar(profesionalId, estado);
    }

    @GetMapping("/agenda")
    public List<Turno> agenda(@RequestParam Long profesionalId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime desde,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return turnoService.buscarAgenda(profesionalId, desde, hasta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> buscarPorId(@PathVariable Long id) {
        Optional<Turno> turno = turnoService.buscarPorId(id);
        if (turno.isPresent()) {
            return ResponseEntity.ok(turno.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody Turno turno) {
        Optional<Turno> creado = turnoService.crear(turno);
        if (creado.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(creado.get());
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turno> actualizar(@PathVariable Long id, @RequestBody Turno datosNuevos) {
        Optional<Turno> actualizado = turnoService.actualizar(id, datosNuevos);
        if (actualizado.isPresent()) {
            return ResponseEntity.ok(actualizado.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = turnoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
