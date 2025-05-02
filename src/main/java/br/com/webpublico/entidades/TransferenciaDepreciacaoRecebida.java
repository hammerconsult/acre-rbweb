package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Desenvolvimento on 22/08/2017.
 */

@Entity
@Audited
@Table(name = "TRANSFERENCIADEPRECIACAREC")
public class TransferenciaDepreciacaoRecebida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaDepreciacaoRecebida() {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA, TipoOperacao.CREDITO);
    }

    public TransferenciaDepreciacaoRecebida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem) {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA, TipoOperacao.CREDITO);
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(estadoBemResultante);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDaDepreciacao());
    }

    public TransferenciaDepreciacaoRecebida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem, Date dataLancamento, Date dataOperacao) {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAORECEBIDA, TipoOperacao.CREDITO);
        if (dataLancamento != null) {
            this.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 15, TipoPrazo.SEGUNDOS));
            this.setDataOperacao(DataUtil.somaPeriodo(dataOperacao, 15, TipoPrazo.SEGUNDOS));
        }
        this.setEstadoInicial(estadoBemInicial);
        this.setEstadoResultante(estadoBemResultante);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoBemInicial.getValorAcumuladoDaDepreciacao());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
