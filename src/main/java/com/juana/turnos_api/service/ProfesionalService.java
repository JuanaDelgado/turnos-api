package com.juana.turnos_api.service;

import com.juana.turnos_api.model.Profesional;
import com.juana.turnos_api.repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;

    public List<Profesional> listar() {
        return profesionalRepository.findAll();
    }

    public Optional<Profesional> buscarPorId(Long id) {
        return profesionalRepository.findById(id);
    }

    public Profesional crear(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }

    public Optional<Profesional> actualizar(Long id, Profesional datosNuevos) {
        Optional<Profesional> encontrado = buscarPorId(id);
        if(encontrado.isPresent()) {
            Profesional profesional = encontrado.get();
            profesional.setNombre(datosNuevos.getNombre());
            profesionalRepository.save(profesional);
        }
        return encontrado;
    }

    public boolean eliminar(Long id) {
        if (profesionalRepository.existsById(id)) {
            profesionalRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
