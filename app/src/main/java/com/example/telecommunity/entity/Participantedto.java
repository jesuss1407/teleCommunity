package com.example.telecommunity.entity;

import java.io.Serializable;

public class Participantedto implements Serializable {

        private String codigoUsuario;
        private String rol;

        public Participantedto() {
            // Constructor vacío necesario para Firebase
        }

        public Participantedto(String codigoUsuario, String rol) {
            this.codigoUsuario = codigoUsuario;
            this.rol = rol;
        }

        // Getters y setters

        public String getCodigoUsuario() {
            return codigoUsuario;
        }

        public void setCodigoUsuario(String codigoUsuario) {
            this.codigoUsuario = codigoUsuario;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

}

