package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoControleLicitacao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ItemRequisicaoCompraDesdobravel implements Comparable<ItemRequisicaoCompraDesdobravel>{

    private ExecucaoContratoItem execucaoContratoItem;
    private ExecucaoProcessoItem execucaoProcessoItem;
    private Integer numero;
    private String descricaoComplementar;
    private ObjetoCompra objetoCompra;
    private UnidadeMedida unidadeMedida;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorUtilizado;
    private BigDecimal valorEstornado;
    private BigDecimal valorDisponivel;
    private List<ItemRequisicaoDeCompra> itensRequisicao;
    private Boolean editando;
    private TipoControleLicitacao tipoControle;

    public ItemRequisicaoCompraDesdobravel(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
        this.itensRequisicao = Lists.newArrayList();
        this.numero = execucaoContratoItem.getItemContrato().getItemAdequado().getNumeroItem();
        this.descricaoComplementar = !Strings.isNullOrEmpty(execucaoContratoItem.getItemContrato().getItemAdequado().getDescricaoComplementar())
            ? execucaoContratoItem.getItemContrato().getItemAdequado().getDescricaoComplementar()
            : execucaoContratoItem.getItemContrato().getItemAdequado().getDescricao();
        this.objetoCompra = execucaoContratoItem.getObjetoCompra();
        this.unidadeMedida = execucaoContratoItem.getItemContrato().getItemAdequado().getUnidadeMedida();
        this.valorUnitario = execucaoContratoItem.getValorUnitario();
        this.quantidade = execucaoContratoItem.getQuantidadeUtilizada();
        this.valorTotal = execucaoContratoItem.getValorTotal();
        this.valorUtilizado = BigDecimal.ZERO;
        this.valorEstornado = BigDecimal.ZERO;
        this.valorDisponivel = BigDecimal.ZERO;
        this.editando = false;
        this.tipoControle = execucaoContratoItem.getItemContrato().getTipoControle();
    }

    public ItemRequisicaoCompraDesdobravel(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
        this.itensRequisicao = Lists.newArrayList();
        this.numero = execucaoProcessoItem.getItemProcessoCompra().getNumero();
        this.descricaoComplementar = !Strings.isNullOrEmpty(execucaoProcessoItem.getItemProcessoCompra().getDescricaoComplementar())
            ? execucaoProcessoItem.getItemProcessoCompra().getDescricaoComplementar()
            : execucaoProcessoItem.getItemProcessoCompra().getDescricao();
        this.objetoCompra = execucaoProcessoItem.getObjetoCompra();
        this.unidadeMedida = execucaoProcessoItem.getItemProcessoCompra().getItemSolicitacaoMaterial().getUnidadeMedida();
        this.valorUnitario = execucaoProcessoItem.getValorUnitario();
        this.quantidade = execucaoProcessoItem.getQuantidade();
        this.valorTotal = execucaoProcessoItem.getValorTotal();
        this.valorUtilizado = BigDecimal.ZERO;
        this.valorEstornado = BigDecimal.ZERO;
        this.valorDisponivel = BigDecimal.ZERO;
        this.editando = false;
        this.tipoControle = execucaoProcessoItem.getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
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

    public BigDecimal getValorUtilizado() {
        return valorUtilizado;
    }

    public void setValorUtilizado(BigDecimal valorUtilizado) {
        this.valorUtilizado = valorUtilizado;
    }

    public BigDecimal getValorEstornado() {
        return valorEstornado;
    }

    public void setValorEstornadoExecucao(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public BigDecimal getValorDisponivel() {
        return valorDisponivel;
    }

    public void setValorDisponivel(BigDecimal valorDisponivel) {
        this.valorDisponivel = valorDisponivel;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public List<ItemRequisicaoDeCompra> getItensRequisicao() {
        return itensRequisicao;
    }

    public void setItensRequisicao(List<ItemRequisicaoDeCompra> itensRequisicao) {
        this.itensRequisicao = itensRequisicao;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Boolean getEditando() {
        return editando;
    }

    public void setEditando(Boolean editando) {
        this.editando = editando;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public BigDecimal getValorTotalItemDesdobravel() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoDeCompra itemReq : getItensRequisicao()) {
            total = total.add(itemReq.getValorTotal());
        }
        return total;
    }

    public String getNumeroDescricao() {
        try {
            return getNumero() + " - " + getObjetoCompra().getDescricao();
        } catch (NullPointerException e) {
            return getObjetoCompra().getDescricao();
        }
    }

    @Override
    public int compareTo(ItemRequisicaoCompraDesdobravel o) {
        if (getNumero() != null && o.getNumero() != null) {
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }
}
