package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 23/09/2015.
 */

@Entity
@Audited
@Etiqueta("Reavaliação de Valor do Bem")
public class ReavaliacaoBem extends EventoBem {

    @ManyToOne
    private LoteReavaliacaoBem loteReavaliacaoBem;

    @Etiqueta("Valor")
    private BigDecimal valor;


    public ReavaliacaoBem() {
        super(TipoEventoBem.LOTEREAVALICAOBEM, TipoOperacao.NENHUMA_OPERACAO);
        this.valor = BigDecimal.ZERO;
    }

    public ReavaliacaoBem(Bem b, LoteReavaliacaoBem lote, EstadoBem estadoInicial, EstadoBem estadoResultante, Date dataLancamento) {
        super(TipoEventoBem.LOTEREAVALICAOBEM, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(b);
        this.loteReavaliacaoBem = lote;
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setDataLancamento(dataLancamento);
    }

    public LoteReavaliacaoBem getLoteReavaliacaoBem() {
        return loteReavaliacaoBem;
    }

    public void setLoteReavaliacaoBem(LoteReavaliacaoBem loteReavaliacaoBem) {
        this.loteReavaliacaoBem = loteReavaliacaoBem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
