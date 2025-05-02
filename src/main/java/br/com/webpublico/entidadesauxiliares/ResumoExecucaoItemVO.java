package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContratoItem;
import br.com.webpublico.entidades.ExecucaoProcessoItem;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;

public class ResumoExecucaoItemVO implements Comparable<ResumoExecucaoItemVO> {

    private Integer numeroLote;
    private Integer numeroItem;
    private String descricao;
    private String especificao;
    private TipoControleLicitacao tipoControle;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValorUnitario;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ResumoExecucaoItemVO(ExecucaoContratoItem itemEx) {
        numeroLote = itemEx.getItemContrato().getItemAdequado().getNumeroLote();
        numeroItem = itemEx.getItemContrato().getItemAdequado().getNumeroItem();
        descricao = itemEx.getItemContrato().getItemAdequado().getDescricao();
        especificao = itemEx.getItemContrato().getItemAdequado().getDescricaoComplementar();
        tipoControle = itemEx.getItemContrato().getTipoControle();
        mascaraQuantidade = itemEx.getMascaraQuantidade();
        mascaraValorUnitario = itemEx.getMascaraValorUnitario();
        quantidade = itemEx.getQuantidadeUtilizada();
        valorUnitario = itemEx.getValorUnitario();
        valorTotal = itemEx.getValorTotal();
    }

    public ResumoExecucaoItemVO(ExecucaoProcessoItem itemEx) {
        numeroLote = itemEx.getItemProcessoCompra().getLoteProcessoDeCompra().getNumero();
        numeroItem = itemEx.getItemProcessoCompra().getNumero();
        descricao = itemEx.getItemProcessoCompra().getDescricao();
        especificao = itemEx.getItemProcessoCompra().getDescricaoComplementar();
        tipoControle = itemEx.getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
        mascaraQuantidade = itemEx.getMascaraQuantidade();
        mascaraValorUnitario = itemEx.getMascaraValorUnitario();
        quantidade = itemEx.getQuantidade();
        valorUnitario = itemEx.getValorUnitario();
        valorTotal = itemEx.getValorTotal();
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroIem) {
        this.numeroItem = numeroIem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEspecificao() {
        return especificao;
    }

    public void setEspecificao(String especificao) {
        this.especificao = especificao;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(TipoMascaraUnidadeMedida mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public int compareTo(ResumoExecucaoItemVO o) {
        try {
            if (o.getNumeroLote() != null && getNumeroLote() != null && o.getNumeroItem() != null && getNumeroItem() != null) {
                return ComparisonChain.start().compare(getNumeroLote(), o.getNumeroLote())
                    .compare(getNumeroItem(), o.getNumeroItem()).result();
            }
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
