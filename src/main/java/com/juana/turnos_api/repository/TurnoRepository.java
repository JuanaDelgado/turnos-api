package com.juana.turnos_api.repository;

import com.juana.turnos_api.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
}
