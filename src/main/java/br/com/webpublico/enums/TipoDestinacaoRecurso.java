/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Arthur
 */
public enum TipoDestinacaoRecurso {

    ORDINARIO("Ordinário"),
    VINCULADO("Vinculado"),
    CONVENIO("Convenio"),
    OPERACAO_CREDITO("Operação de Crédito");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoDestinacaoRecurso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
