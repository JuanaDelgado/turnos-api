package com.juana.turnos_api.controller;

import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.service.ProfesionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profesionales")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    public ProfesionalController(ProfesionalService profesionalService) {
        this.profesionalService = profesionalService;
    }

    @GetMapping
    public List<Profesional> listar() {
        return profesionalService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profesional> buscarPorId(@PathVariable Long id) {
        Optional<Profesional> profesional = profesionalService.buscarPorId(id);
        if (profesional.isPresent()) {
            return ResponseEntity.ok(profesional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Profesional> crear(@RequestBody Profesional profesional) {
        Profesional creado = profesionalService.crear(profesional);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profesional> actualizar (@PathVariable Long id, @RequestBody Profesional datosNuevos) {
        Optional<Profesional> actualizado = profesionalService.actualizar(id, datosNuevos);
        if (actualizado.isPresent()) {
            return ResponseEntity.ok(actualizado.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = profesionalService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
