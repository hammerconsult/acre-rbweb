/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author leonardo
 */
public enum SituacaoSolicitacao implements EnumComDescricao {

    AGUARDANDOPAGAMENTO("Aguardando Pagamento"),
    PAGO("Pago"),
    ABERTO("Aberto"),
    CANCELADO("Cancelado"),
    EMITIDO("Emitido"),
    NAONECESSITAPAGAMENTO("Não Necessita de Pagamento");

    private SituacaoSolicitacao(String descricao) {
        this.descricao = descricao;
    }


    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static SituacaoSolicitacao valorPelaDescricao(String descricao) {
        if (descricao.equals(AGUARDANDOPAGAMENTO.getDescricao())){
            return AGUARDANDOPAGAMENTO;
        } else if (descricao.equals(PAGO.getDescricao())){
            return PAGO;
        } else {
            return NAONECESSITAPAGAMENTO;
        }
    }
}
