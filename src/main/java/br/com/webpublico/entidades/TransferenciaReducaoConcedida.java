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
@Table(name = "TRANSFERENCIAREDUCCONCED")
public class TransferenciaReducaoConcedida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaReducaoConcedida() {
        super(TipoEventoBem.TRANSFERENCIAREDUCAOCONCEDIDA, TipoOperacao.DEBITO);
    }

    public TransferenciaReducaoConcedida(EstadoBem estadoBem, Bem bem, EstadoBem estadoInicial) {
        super(TipoEventoBem.TRANSFERENCIAREDUCAOCONCEDIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoBem);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoInicial.getValorAcumuladoDeAjuste());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
