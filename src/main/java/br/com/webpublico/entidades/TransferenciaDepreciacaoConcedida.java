package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
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
@Table(name = "TRANSFERENCIADEPRCONCEDIDA")
public class TransferenciaDepreciacaoConcedida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaDepreciacaoConcedida() {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA, TipoOperacao.DEBITO);
    }

    public TransferenciaDepreciacaoConcedida(EstadoBem estadoBem, Bem bem, EstadoBem estadoInicial) {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA, TipoOperacao.DEBITO);
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoBem);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoInicial.getValorAcumuladoDaDepreciacao());
    }


    public TransferenciaDepreciacaoConcedida(EstadoBem estadoBem, Bem bem, EstadoBem estadoInicial, Date dataLancamento, Date dataOperacao) {
        super(TipoEventoBem.TRANSFERENCIADEPRECIACAOCONCEDIDA, TipoOperacao.DEBITO);
        if (dataLancamento != null) {
            this.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 10, TipoPrazo.SEGUNDOS));
            this.setDataOperacao(DataUtil.somaPeriodo(dataOperacao, 10, TipoPrazo.SEGUNDOS));
        }
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoBem);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(estadoInicial.getValorAcumuladoDaDepreciacao());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
