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
@Table(name = "ESTORTRANSFEXAUSTAOREC")
public class EstornoTransferenciaExaustaoRecebida extends EventoBem {

    public EstornoTransferenciaExaustaoRecebida() {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAEXAUSTAORECEBIDA, TipoOperacao.DEBITO);
    }

    public EstornoTransferenciaExaustaoRecebida(EstadoBem estadoBemInicial, EstadoBem novoEstado, Bem bem) {
        super(TipoEventoBem.ESTORNOTRANSFERENCIAEXAUSTAORECEBIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(novoEstado);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDaExaustao());
    }
}
