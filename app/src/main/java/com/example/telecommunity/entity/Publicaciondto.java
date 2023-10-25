package com.example.telecommunity.entity;

public class Publicaciondto {
    private String nombre;
    private long horaCreacion;
    private String contenido;
    private String urlImagen;
    private double latitud;
    private double longitud;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String fotoUsuario;

    public Publicaciondto() {
    }

    // Constructor con argumentos
    public Publicaciondto(String nombre, long horaCreacion, String contenido, String urlImagen, double latitud, double longitud, String userName) {
        // ... (inicialización de campos)
    }

    public Publicaciondto(String nombre, long horaCreacion, String contenido, String urlImagen, double latitud, double longitud, String userName, String userApellido, String userFotoPerfil) {
        this.nombre = nombre;
        this.horaCreacion = horaCreacion;
        this.contenido = contenido;
        this.urlImagen = urlImagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombreUsuario = userName; // Asignar el valor de userName al campo nombreUsuario
        this.apellidoUsuario = userApellido;
        this.fotoUsuario = userFotoPerfil;
    }


    // Getters y setters para cada campo
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getHoraCreacion() {
        return horaCreacion;
    }

    public void setHoraCreacion(long horaCreacion) {
        this.horaCreacion = horaCreacion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }


    /*
    public Publicaciondto(int photoResId, String usuario, String actividad, String date, String timestamp, int comentarios, String location, String contenido, int fotoId, double latitud, double longitud) {
        this.photoResId = photoResId;
        this.usuario = usuario;
        this.actividad = actividad;
        this.date = date;
        this.timestamp = timestamp;
        this.location = location;
        this.contenido = contenido;
        this.fotoId = fotoId;
        this.latitud = latitud;
        this.longitud = longitud;
    }
    */
}









