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
@Table(name = "ESTORTRANSFREDUCAOREC")
public class EstornoTransferenciaReducaoRecebida extends EventoBem {

    public EstornoTransferenciaReducaoRecebida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAREDUCAORECEBIDA, TipoOperacao.DEBITO);
    }

    public EstornoTransferenciaReducaoRecebida(EstadoBem estadoBemInicial, EstadoBem novoEstado, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAREDUCAORECEBIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(novoEstado);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDeAjuste());
    }

    @Override
    public Boolean ehEstorno() {
        return Boolean.TRUE;
    }
}
