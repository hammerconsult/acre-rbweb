package br.com.webpublico.enums;

/**
 * Created by Fabio on 26/05/2017.
 */
public enum TipoParcelaRevisaoDA {

    AGUARDANDO_REVISAO("Aguardando Revisão", SituacaoParcela.AGUARDANDO_REVISAO, SituacaoParcela.REVISADO, SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA),
    DIVIDA_ATIVA_EM_REVISAO("Dívida Ativa Em Revisão", SituacaoParcela.DIVIDA_ATIVA_EM_REVISAO, SituacaoParcela.EM_ABERTO, SituacaoParcela.EM_ABERTO),
    NOVO_DEBITO("Em Aberto", SituacaoParcela.EM_ABERTO, SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA, SituacaoParcela.CANCELAMENTO);

    private String descricao;
    private SituacaoParcela situacaoParcela;
    private SituacaoParcela situacaoParcelaFinal;
    private SituacaoParcela situacaoParcelaEstorno;

    TipoParcelaRevisaoDA(String descricao, SituacaoParcela situacaoParcela, SituacaoParcela situacaoParcelaFinal, SituacaoParcela situacaoParcelaEstorno) {
        this.descricao = descricao;
        this.situacaoParcela = situacaoParcela;
        this.situacaoParcelaEstorno = situacaoParcelaEstorno;
        this.situacaoParcelaFinal = situacaoParcelaFinal;
    }

    public String getDescricao() {
        return descricao;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public SituacaoParcela getSituacaoParcelaEstorno() {
        return situacaoParcelaEstorno;
    }

    public SituacaoParcela getSituacaoParcelaFinal() {
        return situacaoParcelaFinal;
    }
}
