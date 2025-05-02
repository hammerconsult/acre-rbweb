package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

/**
 * Created by William on 23/10/2015.
 */

@Entity
@Audited
public class EfetivacaoReavaliacaoBem extends EventoBem {

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Reavaliação")
    @OneToOne
    private ReavaliacaoBem reavaliacaoBem;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote")
    @ManyToOne
    private LoteEfetivacaoReavaliacaoBem lote;

    public EfetivacaoReavaliacaoBem() {
        super(TipoEventoBem.EFETIVACAOREAVALICAOBEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ReavaliacaoBem getReavaliacaoBem() {
        return reavaliacaoBem;
    }

    public void setReavaliacaoBem(ReavaliacaoBem reavaliacaoBem) {
        this.reavaliacaoBem = reavaliacaoBem;
    }

    public LoteEfetivacaoReavaliacaoBem getLote() {
        return lote;
    }

    public void setLote(LoteEfetivacaoReavaliacaoBem lote) {
        this.lote = lote;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return reavaliacaoBem.getValor();
    }
}
