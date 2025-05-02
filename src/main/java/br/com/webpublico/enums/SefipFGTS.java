/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum SefipFGTS {

    NO_PRAZO("1", "No Prazo"),
    EM_ATRASO("2", "Em Atraso"),
    EM_ATRASO_ACAO_FISCAL("3", "Em Atraso - Ação Fiscal"),
    INDIVIDUALIZACAO("5", "Individualização"),
    INDIVIDUALIZACAO_ACAO_FISCAL("6", "Individualização - Ação Fiscal"),
    NAO_INFORMADO(" ", "Não Informado");

    private String codigo;
    private String descricao;

    private SefipFGTS(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
