package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.ParametroSaudDocumento;

import java.io.Serializable;

public class WsParametroSaudDocumento implements Serializable {

    private Long id;
    private String descricao;
    private String extensoesPermitidas;
    private Boolean ativo;

    public WsParametroSaudDocumento() {
    }

    public WsParametroSaudDocumento(ParametroSaudDocumento parametroSaudDocumento) {
        this.id = parametroSaudDocumento.getId();
        this.descricao = parametroSaudDocumento.getDescricao();
        this.extensoesPermitidas = parametroSaudDocumento.getExtensoesPermitidas();
        this.ativo = parametroSaudDocumento.getAtivo();
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

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
