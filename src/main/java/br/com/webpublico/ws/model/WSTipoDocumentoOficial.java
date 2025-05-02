package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.TipoDoctoOficial;

import java.io.Serializable;

public class WSTipoDocumentoOficial implements Serializable {
    private Long id;
    private String descricao;
    private String tipoValidacao;

    public WSTipoDocumentoOficial() {
    }

    public WSTipoDocumentoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.id = tipoDoctoOficial.getId();
        this.descricao = tipoDoctoOficial.getDescricao();
        this.tipoValidacao = tipoDoctoOficial.getTipoValidacaoDoctoOficial().name();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoValidacao() {
        return tipoValidacao;
    }

    public void setTipoValidacao(String tipoValidacao) {
        this.tipoValidacao = tipoValidacao;
    }
}
