package com.juana.turnos_api.controller;

import com.juana.turnos_api.dto.TurnoRequest;
import com.juana.turnos_api.dto.TurnoResponse;
import com.juana.turnos_api.model.EstadoTurno;
import com.juana.turnos_api.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    public List<TurnoResponse> listar(@RequestParam(required = false) Long profesionalId,
                                      @RequestParam(required = false)EstadoTurno estado) {
        return turnoService.listar(profesionalId, estado);
    }

    @GetMapping("/paginado")
    public PagedModel<TurnoResponse> listarPaginado(@PageableDefault(size = 5, sort = "fechaHora")Pageable pageable) {
        return new PagedModel<>(turnoService.listarPaginado(pageable));
    }

    @GetMapping("/agenda")
    public List<TurnoResponse> agenda(@RequestParam Long profesionalId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime desde,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return turnoService.buscarAgenda(profesionalId, desde, hasta);
    }

    @GetMapping("/{id}")
    public TurnoResponse buscarPorId(@PathVariable Long id) {
        return turnoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> crear(@RequestBody TurnoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.crear(request));
    }

    @PatchMapping("/{id}/cancelar")
    public TurnoResponse cancelar(@PathVariable Long id) {
        return turnoService.cancelar(id);
    }

    @PatchMapping("/{id}/confirmar")
    public TurnoResponse confirmar(@PathVariable Long id) {
        return turnoService.confirmar(id);
    }

}
