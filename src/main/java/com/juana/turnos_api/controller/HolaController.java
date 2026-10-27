package com.juana.turnos_api.controller;

import com.juana.turnos_api.model.Profesional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @GetMapping("/hola")
    public String hola() {
        return "¡Hola mundo desde mi API!";
    }

    @GetMapping("/hola/{nombre}")
    public String holaConNombre(@PathVariable String nombre) {
        return "¡Hola " + nombre + "!";
    }

    @GetMapping("/ejemplo/profesional")
    public Profesional profesionalDeEjemplo() {
        return new Profesional(1L, "Sebastían Brey", "Cirujano");
    }
}
