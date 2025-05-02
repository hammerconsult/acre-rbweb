package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UsuarioSistema;

import java.io.Serializable;

public class VOUsuarioArrecadacaoTributaria implements Serializable {
    private UsuarioSistema usuario;
    private Boolean disponivel;

    public VOUsuarioArrecadacaoTributaria(UsuarioSistema usuario, Boolean disponivel) {
        this.usuario = usuario;
        this.disponivel = disponivel;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Boolean isDisponivel() {
        return disponivel != null ? disponivel : Boolean.FALSE;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}
