/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

/**
 * Relaciona os tipos possíveis de lançamentos contábeis e a natureza das contas contábeis
 * (DÉBITO ou CRÉDITO)
 *
 * @author arthur
 */
public enum TipoLancamentoContabil {
    DEBITO("Débito"),
    CREDITO("Crédito");
    private String descricao;

    @Override
    public String toString() {
        return descricao;
    }

    private TipoLancamentoContabil(String descricao) {
        this.descricao = descricao;
    }

}
