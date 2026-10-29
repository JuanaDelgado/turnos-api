package com.juana.turnos_api.model;

public class Profesional {

    private Long id;
    private String nombre;
    private String especialidad;

    public Profesional() {
    }

    public Profesional(Long id, String nombre, String especialidad) {
        setId(id);
        setNombre(nombre);
        setEspecialidad(especialidad);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() { return especialidad; }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}
