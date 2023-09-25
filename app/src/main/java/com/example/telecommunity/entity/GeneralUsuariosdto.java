package com.example.telecommunity.entity;

public class GeneralUsuariosdto {

    private int codigo;
    private String nombre;
    private String condicion;
    private String link;
    private int estado; //activo, inactivo

    public GeneralUsuariosdto(int codigo, String nombre,  String condicion, int estado, String link) {
        this.setCodigo(codigo);
        this.setNombre(nombre);
        this.setCondicion(condicion);
        this.setEstado(estado);
        this.setLink(link);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
