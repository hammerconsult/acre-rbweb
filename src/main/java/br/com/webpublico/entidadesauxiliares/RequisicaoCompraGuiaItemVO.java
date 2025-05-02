package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class RequisicaoCompraGuiaItemVO {

    private Material material;
    private ItemContrato itemContrato;
    private ItemRequisicaoDeCompra itemRequisicaoCompra;
    private Contrato contrato;
    private BigDecimal quantidadeRequisicao;
    private BigDecimal quantidadeExecucao;
    private BigDecimal quantidadeDisponivel;
    private BigDecimal quantidadeOutraRequisicao;

    public RequisicaoCompraGuiaItemVO() {
        quantidadeExecucao = BigDecimal.ZERO;
        quantidadeDisponivel = BigDecimal.ZERO;
        quantidadeOutraRequisicao = BigDecimal.ZERO;
        quantidadeRequisicao = BigDecimal.ZERO;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public BigDecimal getQuantidadeExecucao() {
        return quantidadeExecucao;
    }

    public void setQuantidadeExecucao(BigDecimal quantidadeExecucao) {
        this.quantidadeExecucao = quantidadeExecucao;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return itemRequisicaoCompra;
    }

    public void setItemRequisicaoCompra(ItemRequisicaoDeCompra itemRequisicaoCompra) {
        this.itemRequisicaoCompra = itemRequisicaoCompra;
    }

    public BigDecimal getQuantidadeOutraRequisicao() {
        return quantidadeOutraRequisicao;
    }

    public void setQuantidadeOutraRequisicao(BigDecimal quantidadeOutraRequisicao) {
        this.quantidadeOutraRequisicao = quantidadeOutraRequisicao;
    }

    public BigDecimal getQuantidadeRequisicao() {
        return quantidadeRequisicao;
    }

    public void setQuantidadeRequisicao(BigDecimal quantidadeRequisicao) {
        this.quantidadeRequisicao = quantidadeRequisicao;
    }

    public BigDecimal getValorUnitario() {
        return itemContrato.getValorUnitario();
    }

    public BigDecimal getValorTotal() {
        if (hasQuantidadeRequisicao()) {
            BigDecimal valorTotal = getQuantidadeRequisicao().multiply(getValorUnitario());
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public boolean hasQuantidadeRequisicao() {
        return quantidadeRequisicao != null && quantidadeRequisicao.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasQuantidadeDisponivel() {
        return quantidadeDisponivel != null && quantidadeDisponivel.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequisicaoCompraGuiaItemVO that = (RequisicaoCompraGuiaItemVO) o;
        return Objects.equals(material, that.material) && Objects.equals(itemContrato, that.itemContrato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, itemContrato);
    }
}
