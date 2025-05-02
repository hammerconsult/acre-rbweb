package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.comum.TermoUso;

import java.io.Serializable;

public class WsTermoUso implements Serializable {

    private Long id;
    private String conteudo;

    public WsTermoUso(TermoUso termoUso) {
        id = termoUso.getId();
        conteudo = termoUso.getConteudo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
