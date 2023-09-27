package com.example.telecommunity.entity;

import java.io.Serializable;

public class GeneralActividadesdto {

    private int numeventos;
    private String name;
    private int numapoyos;
    private int estado; //En curso,Finalizado,Eliminado...
    private String link;

    public GeneralActividadesdto(int numeventos, String name, int numapoyos, int estado, String link) {
        this.numeventos = numeventos;
        this.name = name;
        this.numapoyos = numapoyos;
        this.estado = estado;
        this.link = link;
    }

    public int getNumeventos() {
        return numeventos;
    }

    public void setNumeventos(int numeventos) {
        this.numeventos = numeventos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumapoyos() {
        return numapoyos;
    }

    public void setNumapoyos(int numapoyos) {
        this.numapoyos = numapoyos;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
