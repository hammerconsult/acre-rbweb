package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Desenvolvimento on 22/08/2017.
 */

@Entity
@Audited
@Table(name = "TRANSFERENCIAEXAUSTAOCONC")
public class TransferenciaExaustaoConcedida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaExaustaoConcedida() {
        super(TipoEventoBem.TRANSFERENCIAEXAUSTAOCONCEDIDA, TipoOperacao.DEBITO);
    }

    public TransferenciaExaustaoConcedida(EstadoBem estadoBem, Bem bem, EstadoBem estadoInicial) {
        super(TipoEventoBem.TRANSFERENCIAEXAUSTAOCONCEDIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoBem);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoInicial.getValorAcumuladoDaExaustao());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
