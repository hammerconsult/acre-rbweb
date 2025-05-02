package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 16/12/13
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Transferência de Bem", genero = "F")
public class TransferenciaBem extends EventoBem {

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote de Transferência de Bem")
    @ManyToOne
    private LoteTransferenciaBem loteTransferenciaBem;

    /**
     * Relacionamento mantido para recuperar a solicitação de transferência por efetivação.
     */
    @ManyToOne
    private LoteEfetivacaoTransferenciaBem loteEfetivacao;

    public TransferenciaBem() {
        super(TipoEventoBem.TRANSFERENCIABEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public TransferenciaBem(Bem b, LoteTransferenciaBem lote, EstadoBem estadoInicial,EstadoBem estadoResultante) {
        super(TipoEventoBem.TRANSFERENCIABEM, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(b);
        this.loteTransferenciaBem = lote;
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        this.setValorDoLancamento(BigDecimal.ZERO);
    }

    public LoteTransferenciaBem getLoteTransferenciaBem() {
        return loteTransferenciaBem;
    }

    public void setLoteTransferenciaBem(LoteTransferenciaBem loteTransferenciaBem) {
        this.loteTransferenciaBem = loteTransferenciaBem;
    }

    public LoteEfetivacaoTransferenciaBem getLoteEfetivacao() {
        return loteEfetivacao;
    }

    public void setLoteEfetivacao(LoteEfetivacaoTransferenciaBem loteEfetivacao) {
        this.loteEfetivacao = loteEfetivacao;
    }
}
