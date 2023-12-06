package com.example.telecommunity.entity;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Donacion implements Serializable {
    private String codigo;
    private com.google.firebase.Timestamp timestamp;
    private String url;

    public Donacion() {
    }



    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
