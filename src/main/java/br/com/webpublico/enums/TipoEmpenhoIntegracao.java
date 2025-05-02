/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author venon
 */
public enum TipoEmpenhoIntegracao {

    SEM_INTEGRACAO("Sem Integração"),
    ACORDO_JUDICIAL("Acordo Judicial"),
    CONFISSAO_PARCELAMENTO("Confissão e Parcelamento"),
    CONVENIO_DESPESA("Convênio de Despesa"),
    CONVENIO_RECEITA("Convênio de Receita"),
    FOLHA_PAGTO_CLT("Folha de Pagamento CLT"),
    FOLHA_PAGTO_RPPS("Folha de Pagamento RPPS"),
    OPERACAO_CREDITO("Operação de Crédito"),
    PRECATORIO("Precatório"),
    RPV("RPV"),
    SUPRIMENTO_FUNDO("Suprimento de Fundo"),
    DIVIDA_PUBLICA("Dívida Pública");
    private String descricao;

    private TipoEmpenhoIntegracao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
