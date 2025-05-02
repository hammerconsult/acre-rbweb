package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 29/08/2017.
 */

@Entity
@Audited
@Table(name = "ESTORTRANSFDEPRECCONC")
public class EstornoTransferenciaDepreciacaoConcedida extends EventoBem {

    public EstornoTransferenciaDepreciacaoConcedida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIADEPRECIACAOCONCEDIDA, TipoOperacao.CREDITO);
    }

    public EstornoTransferenciaDepreciacaoConcedida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIADEPRECIACAOCONCEDIDA, TipoOperacao.CREDITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(estadoBemResultante);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemResultante.getValorAcumuladoDaDepreciacao());
    }

    @Override
    public Boolean ehEstorno() {
        return Boolean.TRUE;
    }
}
