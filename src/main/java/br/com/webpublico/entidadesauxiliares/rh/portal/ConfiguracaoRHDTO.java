package br.com.webpublico.entidadesauxiliares.rh.portal;

import java.io.Serializable;

/**
 * Created by William on 04/12/2017.
 */
public class ConfiguracaoRHDTO implements Serializable {

    private Long id;
    private Boolean permitirAcessoPortal;
    private Boolean exibirMenuAvaliacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPermitirAcessoPortal() {
        return permitirAcessoPortal;
    }

    public void setPermitirAcessoPortal(Boolean permitirAcessoPortal) {
        this.permitirAcessoPortal = permitirAcessoPortal;
    }

    public Boolean getExibirMenuAvaliacao() {
        return exibirMenuAvaliacao;
    }

    public void setExibirMenuAvaliacao(Boolean exibirMenuAvaliacao) {
        this.exibirMenuAvaliacao = exibirMenuAvaliacao;
    }
}
