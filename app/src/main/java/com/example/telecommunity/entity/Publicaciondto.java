package com.example.telecommunity.entity;

public class Publicaciondto {
    private int photoResId;
    private String usuario;
    private String actividad;
    private String date;
    private String timestamp;
    private int comentarios;
    private String location;
    private String contenido;
    private int fotoId;

    public Publicaciondto(int photoResId, String usuario, String actividad, String date, String timestamp, int comentarios, String location, String contenido, int fotoId) {
        this.photoResId = photoResId;
        this.usuario = usuario;
        this.actividad = actividad;
        this.date = date;
        this.timestamp = timestamp;
        this.comentarios = comentarios;
        this.location = location;
        this.contenido = contenido;
        this.fotoId = fotoId;
    }


    public int getPhotoResId() {
        return photoResId;
    }

    public void setPhotoResId(int photoResId) {
        this.photoResId = photoResId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getComentarios() {
        return comentarios;
    }

    public void setComentarios(int comentarios) {
        this.comentarios = comentarios;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getFotoId() {
        return fotoId;
    }

    public void setFotoId(int fotoId) {
        this.fotoId = fotoId;
    }
}
