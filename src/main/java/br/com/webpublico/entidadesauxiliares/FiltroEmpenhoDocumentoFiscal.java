package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.Util;

import java.util.List;

public class FiltroEmpenhoDocumentoFiscal {

    private Long idDoctoFiscalLiquidacao;
    private Long idRequisicao;
    private List<Long> idsItemRequisicao;

    public FiltroEmpenhoDocumentoFiscal(Long idRequisicao, List<Long> idsItemRequisicao) {
        this.idRequisicao = idRequisicao;
        this.idsItemRequisicao = idsItemRequisicao;
    }

    public Long getIdDoctoFiscalLiquidacao() {
        return idDoctoFiscalLiquidacao;
    }

    public void setIdDoctoFiscalLiquidacao(Long idDoctoFiscalLiquidacao) {
        this.idDoctoFiscalLiquidacao = idDoctoFiscalLiquidacao;
    }

    public Long getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(Long idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public String getCondicaoSql() {
        String condicao = "";
        if (idRequisicao != null) {
            condicao += " and req.id = " + idRequisicao;
        }
        if (idDoctoFiscalLiquidacao != null) {
            condicao += " and dfec.doctofiscalliquidacao_id = " + idDoctoFiscalLiquidacao;
        }
        if (!Util.isListNullOrEmpty(idsItemRequisicao)) {
            StringBuilder idsItem = new StringBuilder();
            String concatId = "";
            for (Long id : idsItemRequisicao) {
                idsItem.append(concatId).append(id);
                concatId = ",";
            }
            condicao += " and irc.id in (" + idsItem + ") ";
        }
        return condicao;

    }
}
