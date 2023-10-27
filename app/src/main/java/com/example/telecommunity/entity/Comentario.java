package com.example.telecommunity.entity;

import java.io.Serializable;

public class Comentario implements Serializable {

    private String nombre;
    private String apellido;
    private long hora;
    private String contenido;


    private String fotoUsuario;

    // Constructor vacío requerido por Firebase
    public Comentario() {
    }

    // Constructor con parámetros
    public Comentario(String nombre, String apellido, long hora, String contenido, String userFotoPerfil) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.hora = hora;
        this.contenido = contenido;
        this.fotoUsuario = userFotoPerfil;
    }

    // Getters y setters

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

}

