/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author André Gustavo
 */
public enum StatusFiscalizacaoRBTrans {

    ANALISE_AUTUACAO("Análise da Autuação"),
    ARQUIVADO("Arquivado"),
    AGUARDANDO_NOTIFICACAO_INFRATOR("Aguardando Notificação do Infrator"),
    AGUARDANDO_PARECER_RECURSO("Aguardando Parecer ao Recurso"),
    AGUARDANDO_PARECER_RECURSO_JARI("Aguardando Parecer ao Recurso JARI"),
    AGUARDANDO_PARECER_RECURSO_CONSELHO("Aguardando Parecer ao Recurso Conselho"),
    AGUARDANDO_NOTIFICACAO_PENALIDADE("Aguardando Notificação de Penalidade"),
    AGUARDANDO_RECURSO_JARI("Aguardando Recurso Jari"),
    AGUARDANDO_CANCELAMENTO_MULTA_JARI("Aguardando Cancelamento da Multa - JARI"),
    CASSACAO_PERMISSAO("Cassação da Pemissão"),
    AGUARDANDO_PAGAMENTO_MULTA("Aguardando Pagamento da Multa"),
    AGUARDANDO_RECURSO_CONSELHO("Aguardando Recurso Conselho"),
    AGUARDANDO_RECURSO("Aguardando Recurso"),
    FINALIZADO("Finalizado");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private StatusFiscalizacaoRBTrans(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }


}
