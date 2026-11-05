package com.juana.turnos_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;

}
