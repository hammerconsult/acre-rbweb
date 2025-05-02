package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParcela;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 04/12/19.
 */

@Entity
@Audited
public class CalculoPagamentoSubvencao extends Calculo {

    @ManyToOne
    private SubvencaoEmpresas pagamentoSubvencao;
    @ManyToOne
    private ProcessoCalculoSubvencao processoCalculoSubvencao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoPagamentoSubvencao", orphanRemoval = true)
    private List<ItensCalculoSubvencao> itensCalculoSubvencao;
    @Enumerated(EnumType.STRING)
    private SituacaoParcela situacaoParcelaGerada;
    @Temporal(TemporalType.DATE)
    private Date vencimentoOriginalParcela;
    private String referencia;
    private String sequenciaParcela;
    private Long quantidadeParcela;
    private Boolean dividaAtiva;
    private Boolean dividaAtivaAjuizada;

    public CalculoPagamentoSubvencao() {
        super.setTipoCalculo(TipoCalculo.PAGAMENTO_SUBVENCAO);
        itensCalculoSubvencao = new ArrayList<>();
    }

    public SubvencaoEmpresas getPagamentoSubvencao() {
        return pagamentoSubvencao;
    }

    public void setPagamentoSubvencao(SubvencaoEmpresas pagamentoSubvencao) {
        this.pagamentoSubvencao = pagamentoSubvencao;
    }

    public ProcessoCalculoSubvencao getProcessoCalculoSubvencao() {
        return processoCalculoSubvencao;
    }

    public void setProcessoCalculoSubvencao(ProcessoCalculoSubvencao processoCalculoSubvencao) {
        this.processoCalculoSubvencao = processoCalculoSubvencao;
    }

    public List<ItensCalculoSubvencao> getItensCalculoSubvencao() {
        return itensCalculoSubvencao;
    }

    public void setItensCalculoSubvencao(List<ItensCalculoSubvencao> itensCalculoSubvencao) {
        this.itensCalculoSubvencao = itensCalculoSubvencao;
    }

    public SituacaoParcela getSituacaoParcelaGerada() {
        return situacaoParcelaGerada;
    }

    public void setSituacaoParcelaGerada(SituacaoParcela situacaoParcelaGerada) {
        this.situacaoParcelaGerada = situacaoParcelaGerada;
    }

    public Date getVencimentoOriginalParcela() {
        return vencimentoOriginalParcela;
    }

    public void setVencimentoOriginalParcela(Date vencimentoOriginalParcela) {
        this.vencimentoOriginalParcela = vencimentoOriginalParcela;
    }

    @Override
    public String getReferencia() {
        return referencia;
    }

    @Override
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
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
}
