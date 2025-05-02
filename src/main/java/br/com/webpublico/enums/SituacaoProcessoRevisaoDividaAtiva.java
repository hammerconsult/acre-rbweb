/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author GhouL
 */
public enum SituacaoProcessoRevisaoDividaAtiva {

    EM_ABERTO("Em Aberto"),
    FINALIZADO("Finalizado"),
    ESTORNADO("Estornado");
    String descricao;

    private SituacaoProcessoRevisaoDividaAtiva(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
