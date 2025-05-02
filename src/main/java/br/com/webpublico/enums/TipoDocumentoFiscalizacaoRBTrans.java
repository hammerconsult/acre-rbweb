/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author AndreGustavo
 */
public enum TipoDocumentoFiscalizacaoRBTrans {

    NOTIFICACAO_INFRATOR("Notificação do Infrator"),
    PARECER_RECURSO("Parecer do Recurso"),
    NOTIFICACAO_PENALIDADE("Notificação Penalidade"),
    NOTIFICACAO_PRECLUSAO_PROCESSO("Preclusão do Processo"),
    SOLICITACAO_CANCELAMENTO_DIVIDA("Solicitação de Cancelamento da Dívida"),
    PARECER_RECURSO_JARI("Parecer do Recurso JARI"),
    PARECER_RECURSO_CONSELHO("Parecer do Recurso Conselho"),
    SOLICITACAO_RESTITUICAO_PAGAMENTO("Solicitação de Restituição de Pagamento"),
    SOLICITACAO_ARQUIVAMENTO("Solicitação de Arquivamento");

    private TipoDocumentoFiscalizacaoRBTrans(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
