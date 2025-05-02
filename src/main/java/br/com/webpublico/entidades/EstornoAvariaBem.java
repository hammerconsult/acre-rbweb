package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 18/11/14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class EstornoAvariaBem extends EventoBem {

    @ManyToOne
    private AvariaBem avariaBem;

    public EstornoAvariaBem() {
        super(TipoEventoBem.ESTORNOAVARIABEM, TipoOperacao.DEBITO);
    }

    public EstornoAvariaBem(AvariaBem avariaBem) {
        super(TipoEventoBem.ESTORNOAVARIABEM, TipoOperacao.DEBITO);
        setAvariaBem(avariaBem);
        setBem(avariaBem.getBem());
        setEstadoInicial(avariaBem.getEstadoResultante());
        setEstadoResultante(avariaBem.getEstadoResultante());
        setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        setValorDoLancamento(avariaBem.getValorDoLancamento());
    }

    @Override
    public Boolean ehEstorno() {
        return true;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return avariaBem != null ? avariaBem.getValorDoLancamento() : BigDecimal.ZERO;
    }

    public AvariaBem getAvariaBem() {
        return avariaBem;
    }

    public void setAvariaBem(AvariaBem avariaBem) {
        this.avariaBem = avariaBem;
    }
}
