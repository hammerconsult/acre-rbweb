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
@Table(name = "TRANSFERENCIAEXAUSTAOREC")
public class TransferenciaExaustaoRecebida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaExaustaoRecebida() {
        super(TipoEventoBem.TRANSFERENCIAEXAUSTAORECEBIDA, TipoOperacao.CREDITO);
    }

    public TransferenciaExaustaoRecebida(EstadoBem estadoBemInicial, EstadoBem estadoResultante, Bem bem) {
        super(TipoEventoBem.TRANSFERENCIAEXAUSTAORECEBIDA, TipoOperacao.CREDITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(estadoResultante);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDaExaustao());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
