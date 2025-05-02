package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.EfetivacaoLevantamentoBem;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.entidades.LevantamentoBemPatrimonial;
import br.com.webpublico.entidades.LoteEfetivacaoLevantamentoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 09/09/15
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class EfetivacaoDepreciacaoBem extends EventoBem {

    @ManyToOne
    private EfetivacaoLevantamentoBem efetivacaoLevantamentoBem;

    public EfetivacaoDepreciacaoBem() {
        super(TipoEventoBem.EFETIVACAODEPRECIACAOBEM, TipoOperacao.CREDITO);
    }

    public EfetivacaoDepreciacaoBem(EfetivacaoLevantamentoBem efetivacaoLevantamentoBem) {
        super(TipoEventoBem.EFETIVACAODEPRECIACAOBEM, TipoOperacao.CREDITO);
        this.efetivacaoLevantamentoBem = efetivacaoLevantamentoBem;
    }

    public EfetivacaoDepreciacaoBem(LoteEfetivacaoLevantamentoBem lote, LevantamentoBemPatrimonial levantamento) {
        super(TipoEventoBem.EFETIVACAODEPRECIACAOBEM, TipoOperacao.CREDITO);
        this.efetivacaoLevantamentoBem.setLevantamento(levantamento);
    }

    public EfetivacaoLevantamentoBem getEfetivacaoLevantamentoBem() {
        return efetivacaoLevantamentoBem;
    }

    public void setEfetivacaoLevantamentoBem(EfetivacaoLevantamentoBem efetivacaoLevantamentoBem) {
        this.efetivacaoLevantamentoBem = efetivacaoLevantamentoBem;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return getEstadoResultante().getValorAcumuladoDaDepreciacao();
    }
}
