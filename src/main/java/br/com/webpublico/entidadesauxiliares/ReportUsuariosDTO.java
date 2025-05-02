package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;


public class ReportUsuariosDTO implements Serializable{

    private String usuario;
    private Integer quantidade;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
