package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 18/08/2017.
 */
@Entity
@Audited
@Etiqueta("Itens da Avaliação de Prorrogação")
public class ItemAvaliacaoProrrogacao extends EventoBem{

    @ManyToOne
    @Etiqueta("Cessão")
    private Cessao cessao;

    @ManyToOne
    @Etiqueta("Avaliação de Prorrogação")
    private AvaliacaoSolicitacaoProrrogacaoCessao avaliacao;

    public ItemAvaliacaoProrrogacao() {
        super(TipoEventoBem.AVALIACAO_PRORROGACAO_CESSAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemAvaliacaoProrrogacao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao, EstadoBem estadoInicial, EstadoBem estadoResultante, Date dataLancamento, Cessao cessao) {
        super(TipoEventoBem.AVALIACAO_PRORROGACAO_CESSAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(cessao.getBem());
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setAvaliacao(avaliacao);
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        this.setDataLancamento(dataLancamento);
        this.setCessao(cessao);
    }

    public Cessao getCessao() {
        return cessao;
    }

    public void setCessao(Cessao cessao) {
        this.cessao = cessao;
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao) {
        this.avaliacao = avaliacao;
    }
}
