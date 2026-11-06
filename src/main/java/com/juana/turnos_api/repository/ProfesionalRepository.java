package com.juana.turnos_api.repository;

import com.juana.turnos_api.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
}
