/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum SituacaoCadastralContabil {

    ATIVO("Ativo"),
    INATIVO("Inativo");
    private String descricao;

    private SituacaoCadastralContabil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
