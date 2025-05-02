package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 30/08/2017.
 */

@Entity
@Audited
@Table(name = "ESTORTRANSFEXAUSTCONC")
public class EstornoTransferenciaExaustaoConcedida extends EventoBem {

    public EstornoTransferenciaExaustaoConcedida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAEXAUSTAOCONCEDIDA, TipoOperacao.CREDITO);
    }

    public EstornoTransferenciaExaustaoConcedida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAEXAUSTAOCONCEDIDA, TipoOperacao.CREDITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(estadoBemResultante);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemResultante.getValorAcumuladoDaExaustao());
    }

    @Override
    public Boolean ehEstorno() {
        return Boolean.TRUE;
    }
}
