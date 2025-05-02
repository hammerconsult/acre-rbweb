package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 28/05/15
 * Time: 09:48
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class CalculoPagamentoJudicial extends Calculo {
    @ManyToOne
    private PagamentoJudicial pagamentoJudicial;
    @ManyToOne
    private ProcessoCalculoJudicial processoCalculoJudicial;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoPagamentoJudicial", orphanRemoval = true)
    private List<ItensCalculoJudicial> itensCalculoJudicial;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcelaGerada;
    @Temporal(TemporalType.DATE)
    private Date vencimentoOriginalParcela;
    private String referencia;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;

    public CalculoPagamentoJudicial() {
        super.setTipoCalculo(TipoCalculo.PROCESSO_COMPENSACAO);
        itensCalculoJudicial = new ArrayList<>();
    }

    public Date getVencimentoOriginalParcela() {
        return vencimentoOriginalParcela;
    }

    public void setVencimentoOriginalParcela(Date vencimentoOriginalParcela) {
        this.vencimentoOriginalParcela = vencimentoOriginalParcela;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public PagamentoJudicial getPagamentoJudicial() {
        return pagamentoJudicial;
    }

    public void setPagamentoJudicial(PagamentoJudicial pagamentoJudicial) {
        this.pagamentoJudicial = pagamentoJudicial;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public SituacaoParcela getSituacaoParcelaGerada() {
        return situacaoParcelaGerada;
    }

    public void setSituacaoParcelaGerada(SituacaoParcela situacaoParcelaGerada) {
        this.situacaoParcelaGerada = situacaoParcelaGerada;
    }

    public ProcessoCalculoJudicial getProcessoCalculoJudicial() {
        return processoCalculoJudicial;
    }

    public void setProcessoCalculoJudicial(ProcessoCalculoJudicial processoCalculoJudicial) {
        this.processoCalculoJudicial = processoCalculoJudicial;
    }

    public List<ItensCalculoJudicial> getItensCalculoJudicial() {
        return itensCalculoJudicial;
    }

    public void setItensCalculoJudicial(List<ItensCalculoJudicial> itensCalculoJudicial) {
        this.itensCalculoJudicial = itensCalculoJudicial;
    }

    public Long getQuantidadeParcela() {
        return quantidadeParcela;
    }

    public void setQuantidadeParcela(Long quantidadeParcela) {
        this.quantidadeParcela = quantidadeParcela;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public Boolean getDividaAtivaAjuizada() {
        return dividaAtivaAjuizada;
    }

    public void setDividaAtivaAjuizada(Boolean dividaAtivaAjuizada) {
        this.dividaAtivaAjuizada = dividaAtivaAjuizada;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoJudicial;
    }

}
