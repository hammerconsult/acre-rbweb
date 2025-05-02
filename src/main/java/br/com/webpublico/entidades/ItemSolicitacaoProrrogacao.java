package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Audited
@Entity
@Etiqueta("Itens da Solicitação de Prorrogação")
public class ItemSolicitacaoProrrogacao extends EventoBem {

    @ManyToOne
    @Etiqueta("Solicitação de Prorrogação")
    private SolicitacaoProrrogacaoCessao solicitacaoProrrogacao;

    @ManyToOne
    @Etiqueta("Cessão")
    private Cessao cessao;

    public ItemSolicitacaoProrrogacao() {
        super(TipoEventoBem.SOLICITACAO_PRORROGACAO_CESSAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemSolicitacaoProrrogacao(Bem bem, SolicitacaoProrrogacaoCessao solicitacao, EstadoBem estadoBem, Date dataLancamento, Cessao cessao) {
        super(TipoEventoBem.SOLICITACAO_PRORROGACAO_CESSAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.setEstadoInicial(estadoBem);
        this.setEstadoResultante(estadoBem);
        this.setSolicitacaoProrrogacao(solicitacao);
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        this.setDataLancamento(dataLancamento);
        this.setCessao(cessao);
    }

    public SolicitacaoProrrogacaoCessao getSolicitacaoProrrogacao() {
        return solicitacaoProrrogacao;
    }

    public void setSolicitacaoProrrogacao(SolicitacaoProrrogacaoCessao solicitacaoProrrogacao) {
        this.solicitacaoProrrogacao = solicitacaoProrrogacao;
    }

    public Cessao getCessao() {
        return cessao;
    }

    public void setCessao(Cessao cessao) {
        this.cessao = cessao;
    }
}
