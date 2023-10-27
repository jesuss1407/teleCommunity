package com.example.telecommunity.entity;

import java.io.Serializable;

public class ActividadDto implements Serializable {

    private int delegadoCode;
    private String nombre;
    private String descripcion;
    private String fotoLink;
    private String id;

    public ActividadDto(String id, int delegadoCodigo, String nombre, String contenido, String fotoLink) {
        this.id = id;
        this.delegadoCode = delegadoCodigo;
        this.nombre = nombre;
        this.descripcion = contenido;
        this.fotoLink = fotoLink;
    }

    public int getDelegadoCode() {
        return delegadoCode;
    }

    public void setDelegadoCode(int delegadoCode) {
        this.delegadoCode = delegadoCode;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoLink() {
        return fotoLink;
    }

    public void setFotoLink(String fotoLink) {
        this.fotoLink = fotoLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
