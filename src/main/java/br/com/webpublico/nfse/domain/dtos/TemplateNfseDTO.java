package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.TipoTemplateNfse;

public class TemplateNfseDTO {
    private Long id;

    private TipoTemplateNfse tipoTemplate;

    private String conteudo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTemplateNfse getTipoTemplate() {
        return tipoTemplate;
    }

    public void setTipoTemplate(TipoTemplateNfse tipoTemplate) {
        this.tipoTemplate = tipoTemplate;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

}
