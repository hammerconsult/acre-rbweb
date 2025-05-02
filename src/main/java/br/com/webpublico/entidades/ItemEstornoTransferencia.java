package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 24/09/14
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemEstornoTransferencia extends EventoBem {

    @ManyToOne
    private SolicitacaoEstornoTransferencia solicitacaoEstorno;

    @ManyToOne
    private EfetivacaoTransferenciaBem efetivacaoTransferencia;

    public ItemEstornoTransferencia() {
        super(TipoEventoBem.ITEMESTORNOTRANSFERENCIA, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemEstornoTransferencia(SolicitacaoEstornoTransferencia s, EfetivacaoTransferenciaBem ef) {
        super(TipoEventoBem.ITEMESTORNOTRANSFERENCIA, TipoOperacao.NENHUMA_OPERACAO);
        this.solicitacaoEstorno = s;
        this.efetivacaoTransferencia = ef;
        this.setBem(ef.getBem());
    }

    public ItemEstornoTransferencia(Bem bem, SolicitacaoEstornoTransferencia solicitacaoEstornoTransferencia, EfetivacaoTransferenciaBem efetivacaoTransferenciaBem, EstadoBem estadoBem) {
        super(TipoEventoBem.ITEMESTORNOTRANSFERENCIA, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.solicitacaoEstorno = solicitacaoEstornoTransferencia;
        this.efetivacaoTransferencia = efetivacaoTransferenciaBem;
        this.setEstadoInicial(estadoBem);
        this.setEstadoResultante(estadoBem);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
    }

    public SolicitacaoEstornoTransferencia getSolicitacaoEstorno() {
        return solicitacaoEstorno;
    }

    public void setSolicitacaoEstorno(SolicitacaoEstornoTransferencia solicitacaoEstorno) {
        this.solicitacaoEstorno = solicitacaoEstorno;
    }

    public EfetivacaoTransferenciaBem getEfetivacaoTransferencia() {
        return efetivacaoTransferencia;
    }

    public void setEfetivacaoTransferencia(EfetivacaoTransferenciaBem efetivacaoTransferencia) {
        this.efetivacaoTransferencia = efetivacaoTransferencia;
    }
}
