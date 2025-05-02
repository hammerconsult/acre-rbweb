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
 * Time: 08:50
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Table(name = "APURACAOVLLIQEXAUSTAO")
public class ApuracaoValorLiquidoExaustao extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação da Baixa Patrimonial")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial;

    public ApuracaoValorLiquidoExaustao() {
        super(TipoEventoBem.APURACAOVALORLIQUIDOEXAUSTAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ApuracaoValorLiquidoExaustao(Bem bem, EstadoBem inicial, EstadoBem resultante) {
        super(TipoEventoBem.APURACAOVALORLIQUIDOEXAUSTAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setEstadoInicial(inicial);
        this.setEstadoResultante(resultante);
        this.setBem(bem);
        this.setValorDoLancamento(inicial.getValorAcumuladoDaExaustao());
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
        return getEstadoInicial().getValorAcumuladoDaExaustao();
    }

    public String getHistoricoRazao(EfetivacaoBaixaPatrimonial efetivacao) {
        return "Registro Patrimonial nº " + getBem().getIdentificacao() +
            " - Efetivação da Baixa de Bem Móvel por Desincorporação nº " + efetivacao.getCodigo() + " - " + efetivacao.getHistorico() + ".";
    }
}
