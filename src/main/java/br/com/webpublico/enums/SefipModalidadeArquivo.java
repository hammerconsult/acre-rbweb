/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author andre
 */
public enum SefipModalidadeArquivo {

    RECOLHIMENTO_FGTS("' '", "Recolhimento ao FGTS e Declaração à Previdência"),
    DECLARACAO_FGTS("1", "Declaração ao FGTS e à Previdência"),
    CONFIRMACAO_INFO_ANTERIORES("9", "Confirmação Informações anteriores - Rec/Decl ao FGTS e Decl à Previdência");

    private String codigo;
    private String descricao;

    private SefipModalidadeArquivo(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
