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

    private Long proximoId = 4L;

    public List<Profesional> listar() {
        return profesionales;
    }

    public Optional<Profesional> buscarPorId(Long id) {
        return profesionales.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Profesional crear(Profesional profesional) {
        profesional.setId(proximoId);
        proximoId++;
        profesionales.add(profesional);
        return profesional;
    }

    public Optional<Profesional> actualizar(Long id, Profesional datosNuevos) {
        Optional<Profesional> encontrado = buscarPorId(id);
        if(encontrado.isPresent()) {
            Profesional profesional = encontrado.get();
            profesional.setNombre(datosNuevos.getNombre());
            profesional.setEspecialidad(datosNuevos.getEspecialidad());
        }
        return encontrado;
    }

    public boolean eliminar(Long id) {
        return profesionales.removeIf(p -> p.getId().equals(id));
    }

}
