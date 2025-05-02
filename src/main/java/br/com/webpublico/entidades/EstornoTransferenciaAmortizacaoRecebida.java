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
@Table(name = "ESTORTRANSFAMORTIZREC")
public class EstornoTransferenciaAmortizacaoRecebida extends EventoBem {

    public EstornoTransferenciaAmortizacaoRecebida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAAMORTIZACAORECEBIDA, TipoOperacao.DEBITO);
    }

    public EstornoTransferenciaAmortizacaoRecebida(EstadoBem estadoBemInicial, EstadoBem novoEstado, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAAMORTIZACAORECEBIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(novoEstado);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDaAmortizacao());
    }
}
