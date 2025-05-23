package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;

public class ItemRequisicaoCompraExecucaoVO implements Comparable<ItemRequisicaoCompraExecucaoVO> {

    private Long id;
    private ItemRequisicaoCompraVO itemRequisicaoCompraVO;
    private ExecucaoContratoItem execucaoContratoItem;
    private ExecucaoContratoItemDotacao execucaoContratoItemDotacao;
    private ExecucaoProcessoItem execucaoProcessoItem;
    private ExecucaoProcessoFonteItem execucaoProcessoFonteItem;
    private ItemReconhecimentoDivida itemReconhecimentoDivida;
    private ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao;
    private RequisicaoCompraEmpenhoVO requisicaoEmpenhoVO;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal quantidadeDisponivel;
    private Boolean ajusteValor;
    private Long criadoEm;
    private BigDecimal valorUtilizado;
    private BigDecimal valorEstornado;
    private BigDecimal valorDisponivel;

    public ItemRequisicaoCompraExecucaoVO() {
        ajusteValor = false;
        quantidade = BigDecimal.ZERO;
        quantidadeDisponivel = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorUtilizado = BigDecimal.ZERO;
        valorEstornado = BigDecimal.ZERO;
        valorDisponivel = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
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

    public void setValorEstornado(BigDecimal valorEstornado) {
        this.valorEstornado = valorEstornado;
    }

    public BigDecimal getValorDisponivel() {
        return valorDisponivel;
    }

    public void setValorDisponivel(BigDecimal valorDisponivel) {
        this.valorDisponivel = valorDisponivel;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemRequisicaoCompraVO getItemRequisicaoCompraVO() {
        return itemRequisicaoCompraVO;
    }

    public void setItemRequisicaoCompraVO(ItemRequisicaoCompraVO itemRequisicaoCompraVO) {
        this.itemRequisicaoCompraVO = itemRequisicaoCompraVO;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
    }

    public ItemContrato getItemContrato() {
        return execucaoContratoItem.getItemContrato();
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
    }

    public ItemReconhecimentoDividaDotacao getItemReconhecimentoDividaDotacao() {
        return itemReconhecimentoDividaDotacao;
    }

    public void setItemReconhecimentoDividaDotacao(ItemReconhecimentoDividaDotacao itemReconhecimentoDividaDotacao) {
        this.itemReconhecimentoDividaDotacao = itemReconhecimentoDividaDotacao;
    }

    public RequisicaoCompraEmpenhoVO getRequisicaoEmpenhoVO() {
        return requisicaoEmpenhoVO;
    }

    public void setRequisicaoEmpenhoVO(RequisicaoCompraEmpenhoVO requisicaoEmpenhoVO) {
        this.requisicaoEmpenhoVO = requisicaoEmpenhoVO;
    }

    public ExecucaoContratoItemDotacao getExecucaoContratoItemDotacao() {
        return execucaoContratoItemDotacao;
    }

    public void setExecucaoContratoItemDotacao(ExecucaoContratoItemDotacao execucaoContratoItemDotacao) {
        this.execucaoContratoItemDotacao = execucaoContratoItemDotacao;
    }

    public ExecucaoProcessoFonteItem getExecucaoProcessoFonteItem() {
        return execucaoProcessoFonteItem;
    }

    public void setExecucaoProcessoFonteItem(ExecucaoProcessoFonteItem execucaoProcessoFonteItem) {
        this.execucaoProcessoFonteItem = execucaoProcessoFonteItem;
    }

    public boolean isExecucaoContrato() {
        return execucaoContratoItem != null;
    }

    public boolean isExecucaoProcesso() {
        return execucaoProcessoItem != null;
    }

    public boolean isReconhecimentoDivida() {
        return itemReconhecimentoDivida != null;
    }

    public BigDecimal getValorUnitario() {
        if (this.valorUnitario == null) {
            return this.getExecucaoContratoItem().getValorUnitario();
        }
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidadeRequisitada) {
        this.quantidade = quantidadeRequisitada;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            this.valorTotal = BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Boolean getAjusteValor() {
        return ajusteValor;
    }

    public void setAjusteValor(Boolean ajusteValor) {
        this.ajusteValor = ajusteValor;
    }

    public Long getIdItemExecucao() {
        if (isExecucaoContrato()) {
            return execucaoContratoItem.getId();
        } else if (isExecucaoProcesso()) {
            return execucaoProcessoItem.getId();
        }
        return itemReconhecimentoDivida.getId();
    }

    public BigDecimal getValorTotalItemExecucao() {
        if (isExecucaoContrato()) {
            return execucaoContratoItem.getValorTotal();
        } else if (isExecucaoProcesso()) {
            return execucaoProcessoItem.getValorTotal();
        }
        return BigDecimal.ZERO;
    }

    public void calcularValorTotal() {
        if (hasQuantidade() && hasValorUnitario()) {
            setValorTotal(quantidade.multiply(valorUnitario));
        } else {
            setValorTotal(BigDecimal.ZERO);
        }
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null;
    }

    public Integer getNumeroExecucao() {
        try {
            if (isExecucaoContrato()) {
                return execucaoContratoItem.getExecucaoContrato().getNumero();
            } else if (isExecucaoProcesso()) {
                return execucaoProcessoItem.getExecucaoProcesso().getNumero();
            }
            return Integer.valueOf(itemReconhecimentoDivida.getReconhecimentoDivida().getNumero());
        } catch (Exception e) {
            return 1;
        }
    }

    public static ItemRequisicaoCompraExecucaoVO toObjeto(ItemRequisicaoCompraExecucao itemReqExec, ItemRequisicaoCompraVO itemReqVO) {
        ItemRequisicaoCompraExecucaoVO novoItemReqExec = new ItemRequisicaoCompraExecucaoVO();
        novoItemReqExec.setItemRequisicaoCompraVO(itemReqVO);
        novoItemReqExec.setExecucaoContratoItem(itemReqExec.getExecucaoContratoItem());
        novoItemReqExec.setExecucaoProcessoItem(itemReqExec.getExecucaoProcessoItem());
        novoItemReqExec.setItemReconhecimentoDivida(itemReqExec.getItemReconhecimentoDivida());
        novoItemReqExec.setQuantidade(itemReqExec.getQuantidade());
        novoItemReqExec.setValorUnitario(itemReqExec.getValorUnitario());
        novoItemReqExec.setValorTotal(itemReqExec.getValorTotal());
        return novoItemReqExec;
    }

    @Override
    public int compareTo(ItemRequisicaoCompraExecucaoVO o) {
        if (getNumeroExecucao() != null && o.getNumeroExecucao() != null) {
            return ComparisonChain.start()
                .compare(getNumeroExecucao(), o.getNumeroExecucao())
                .result();
        }
        return 0;
    }
}
