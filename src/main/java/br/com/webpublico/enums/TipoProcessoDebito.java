/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

public enum TipoProcessoDebito {

    CANCELAMENTO("Cancelamento", "cancelamento", SituacaoParcela.CANCELAMENTO),
    COMPENSACAO("Compensação", "compensacao", SituacaoParcela.COMPENSACAO),
    REMISSAO("Remissão", "remissao", SituacaoParcela.REMISSAO),
    PRESCRICAO("Prescrição", "prescricao", SituacaoParcela.PRESCRICAO),
    DECADENCIA("Decadência", "decadencia", SituacaoParcela.DECADENCIA),
    SUSPENSAO("Suspensão", "suspensao", SituacaoParcela.SUSPENSAO),
    ANISTIA("Anistia", "anistia", SituacaoParcela.ANISTIA),
    DACAO("Dação", "dacao", SituacaoParcela.DACAO),
    REATIVACAO("Reativação", "suspensao-reativacao", SituacaoParcela.EM_ABERTO),
    ISENCAO("Isenção", "isencao", SituacaoParcela.ISENTO),
    DESISTENCIA("Desistência", "desistencia", SituacaoParcela.DESISTENCIA),
    BAIXA("Baixa", "baixa", SituacaoParcela.BAIXADO),
    ATUALIZACAO_MONETARIA("Atualização Monetária", "atualizacao", SituacaoParcela.EM_ABERTO),
    DEDUCAO_ACRESCIMO("Dedução de Acrescimos", "deducao-acrescimo", SituacaoParcela.EM_ABERTO),
    ALTERACAO_VENCIMENTO("Alteração de Vencimento", "alteracao-vencimento", SituacaoParcela.EM_ABERTO);
    private String descricao;
    private String url;
    private SituacaoParcela situacaoParcela;

    private TipoProcessoDebito(String descricao, String url, SituacaoParcela situacaoParcela) {
        this.descricao = descricao;
        this.situacaoParcela = situacaoParcela;
        this.url = url;
    }

    public static TipoProcessoDebito getBySituacaoParcela(SituacaoParcela situacaoParcela) {
        for (TipoProcessoDebito tipoProcessoDebito : values()) {
            if (tipoProcessoDebito.situacaoParcela.equals(situacaoParcela)) {
                return tipoProcessoDebito;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUrl() {
        return url;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
