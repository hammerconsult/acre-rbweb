/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum EntidadeOCC {

    CONTAFINANCEIRA("Conta Financeira"),
    CONTADESPESA("Conta de Despesa"),
    CONTARECEITA("Conta de Receita"),
    CONTACONTABIL("Conta Contábil"),
    DESTINACAO("Conta de Destinação"),
    CONTAEXTRAORCAMENTARIA("Conta Extraorçamentária"),
    NATUREZADIVIDAPUBLICA("Natureza da Dívida Pública"),
    TIPOCLASSEPESSOA("Tipo Classe Pessoa"),
    TIPOPASSIVOATUARIAL("Tipo Passivo Atuarial"),
    GRUPOMATERIAL("Grupo Material"),
    GRUPOBEM("Grupo Patrimonial"),
    ORIGEM_RECURSO("Origem de Recurso");

    private String descricao;

    private EntidadeOCC(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
