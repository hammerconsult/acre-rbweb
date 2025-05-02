package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOUsuarioIP implements Serializable {
    private String usuario;
    private String ip;

    public VOUsuarioIP(String usuario, String ip) {
        this.usuario = usuario;
        this.ip = ip;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getIp() {
        return ip;
    }
}
