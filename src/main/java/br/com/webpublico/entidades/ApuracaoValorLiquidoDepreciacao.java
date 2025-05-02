package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 11/11/14
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "APURACAOVLLIQDEPRECIACAO")
public class ApuracaoValorLiquidoDepreciacao extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação da Baixa Patrimonial")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;

    public ApuracaoValorLiquidoDepreciacao() {
        super(TipoEventoBem.APURACAOVALORLIQUIDODEPRECIACAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ApuracaoValorLiquidoDepreciacao(Bem bem, EstadoBem inicial, EstadoBem resultante) {
        super(TipoEventoBem.APURACAOVALORLIQUIDODEPRECIACAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.setEstadoInicial(inicial);
        this.setEstadoResultante(resultante);
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        this.setValorDoLancamento(inicial.getValorAcumuladoDaDepreciacao());
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixaPatrimonial() {
        return efetivacaoBaixaPatrimonial;
    }

    public void setEfetivacaoBaixaPatrimonial(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial) {
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return getEstadoInicial().getValorAcumuladoDaDepreciacao();
    }

    public String getHistoricoRazao(EfetivacaoBaixaPatrimonial efetivacao) {
        String descricaoTipoBaixa = efetivacao.isSolicitacaoBaixaPorAlienacao() ? "Alienação" : "Desincorporação";
        return "Registro Patrimonial nº " + getBem().getIdentificacao() + " - Efetivação da Baixa de Bem Móvel por " + descricaoTipoBaixa + " nº " + efetivacao.getCodigo() + " - " + efetivacao.getHistorico() + ".";
    }
}
