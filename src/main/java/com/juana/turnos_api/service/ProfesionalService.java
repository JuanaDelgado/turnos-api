package com.juana.turnos_api.service;

import com.juana.turnos_api.model.Profesional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalService {

    private final List<Profesional> profesionales = new ArrayList<>(List.of(
            new Profesional(1L, "Sebastian Brey", "Cirujano"),
            new Profesional(2L, "Antonia Blanco", "Dermatóloga"),
            new Profesional(3L, "Verónica Martínez", "Traumatóloga")
    ));

    public List<Profesional> listar() {
        return profesionales;
    }

    public Profesional buscarPorId(Long id) {
        return profesionales.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
