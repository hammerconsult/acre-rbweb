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
 * User: Desenvolvimento
 * Date: 15/01/15
 * Time: 09:24
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Table(name = "APURACAOVLLIQAJUSTE")
public class ApuracaoValorLiquidoAjuste extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação da Baixa Patrimonial")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;

    public ApuracaoValorLiquidoAjuste() {
        super(TipoEventoBem.APURACAOVALORLIQUIDOAJUSTE, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ApuracaoValorLiquidoAjuste(Bem bem, EstadoBem inicial, EstadoBem resultante) {
        super(TipoEventoBem.APURACAOVALORLIQUIDOAJUSTE, TipoOperacao.NENHUMA_OPERACAO);
        this.setEstadoInicial(inicial);
        this.setEstadoResultante(resultante);
        this.setBem(bem);
        this.setValorDoLancamento(inicial.getValorAcumuladoDeAjuste());
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixaPatrimonial() {
        return efetivacaoBaixaPatrimonial;
    }

    public void setEfetivacaoBaixaPatrimonial(EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial) {
        this.efetivacaoBaixaPatrimonial = efetivacaoBaixaPatrimonial;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return getEstadoInicial().getValorAcumuladoDeAjuste();
    }

    public String getHistoricoRazao(EfetivacaoBaixaPatrimonial efetivacao) {
        return "Registro Patrimonial nº " + getBem().getIdentificacao() +
            " - Efetivação da Baixa de Bem Móvel por Desincorporação nº " + efetivacao.getCodigo() + " - " + efetivacao.getHistorico() + ".";
    }
}
