package com.juana.turnos_api.controller;

import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.service.ProfesionalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Profesional buscarPorId(@PathVariable Long id) {
        return profesionalService.buscarPorId(id);
    }

}
