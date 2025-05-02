package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by Desenvolvimento on 22/08/2017.
 */

@Entity
@Audited
public class TransferenciaBemConcedida extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação de Transferência")
    private LoteEfetivacaoTransferenciaBem efetivacaoTransferencia;

    public TransferenciaBemConcedida() {
        super(TipoEventoBem.TRANSFERENCIABEMCONCEDIDA, TipoOperacao.CREDITO);
    }

    public TransferenciaBemConcedida(EstadoBem novoEstado, Bem bem, EstadoBem inicial) {
        super(TipoEventoBem.TRANSFERENCIABEMCONCEDIDA, TipoOperacao.CREDITO);
        this.setEstadoInicial(inicial);
        this.setEstadoResultante(novoEstado);
        this.setBem(bem);
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.setValorDoLancamento(inicial.getValorOriginal());
    }

    public LoteEfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
