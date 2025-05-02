package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

/**
 * Created by william on 19/09/17.
 */
public class ConfiguracaoTemplateNfseDTO implements Serializable {
    private Long id;
    private String tipoTemplateEmailNfse;
    private String conteudo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoTemplateEmailNfse() {
        return tipoTemplateEmailNfse;
    }

    public void setTipoTemplateEmailNfse(String tipoTemplateEmailNfse) {
        this.tipoTemplateEmailNfse = tipoTemplateEmailNfse;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
