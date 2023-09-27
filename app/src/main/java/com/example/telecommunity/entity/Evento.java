package com.example.telecommunity.entity;

import java.io.Serializable;

public class Evento implements Serializable {
    private String titulo;
    private String descripcion;
    private String hora;
    private String creador;

    public Evento(String titulo, String descripcion, String hora, String creador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.hora = hora;
        this.creador = creador;
    }

    // Agrega los métodos getter para acceder a los atributos

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getHora() {
        return hora;
    }

    public String getCreador() {
        return creador;
    }
}


