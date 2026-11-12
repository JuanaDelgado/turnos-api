package com.juana.turnos_api.repository;

import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    List<Turno> findByProfesionalId(Long profesionalId);

    List<Turno> findByEstado(EstadoTurno estado);

    List<Turno> findByProfesionalIdAndEstado(Long profesionalId, EstadoTurno estado);

    boolean existsByProfesionalIdAndFechaHora(Long profesionalId, LocalDateTime fechaHora);

    @Query("SELECT t FROM Turno t " +
    "WHERE t.profesional.id = :profesionalId " +
    "AND t.fechaHora BETWEEN :desde AND :hasta " +
    "ORDER BY t.fechaHora")
    List<Turno> buscarAgendaDeProfesional(@Param("profesionalId") Long profesionalId,
                                          @Param("desde") LocalDateTime desde,
                                          @Param("hasta") LocalDateTime hasta);
}
