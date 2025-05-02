/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Wellington
 */
public enum SituacaoAjuizamento {
    PROTOCOLADO("Protocolado"),
    EM_COBRANCA("Em cobrança"),
    PRAZO_RECURSAL("Prazo recursal"),
    CANCELAMENTO_POR_INDEFERIMENTO("Cancelamento por Indeferimento"),
    LEILAO("Leilão"),
    ACORDO_DE_PAGAMENTO("Acordo de pagamento"),
    BAIXADO_VIA_COBRANCA_JUDICIAL("Baixado via Cobrança Judicial"),
    BAIXADO_VIA_ACORDO("Baixado via Acordo");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoAjuizamento(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
