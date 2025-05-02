package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemRequisicaoMaterial;
import br.com.webpublico.entidades.Material;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GuiaDistribuicaoItemVO implements Comparable<GuiaDistribuicaoItemVO> {

    private RequisicaoCompraGuiaItemVO itemReqCompra;
    private ItemRequisicaoMaterial itemRequisicaoMaterial;
    private BigDecimal quantidade;

    public GuiaDistribuicaoItemVO() {
        this.quantidade = BigDecimal.ZERO;
    }

    public ItemRequisicaoMaterial getItemRequisicaoMaterial() {
        return itemRequisicaoMaterial;
    }

    public void setItemRequisicaoMaterial(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        this.itemRequisicaoMaterial = itemRequisicaoMaterial;
    }

    public RequisicaoCompraGuiaItemVO getItemReqCompra() {
        return itemReqCompra;
    }

    public void setItemReqCompra(RequisicaoCompraGuiaItemVO itemReqCompra) {
        this.itemReqCompra = itemReqCompra;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
    public BigDecimal getValorUnitario(){
        return itemReqCompra.getValorUnitario();
    }

    public BigDecimal getValorTotal() {
        if (hasQuantidade()) {
            BigDecimal valorTotal = getQuantidade().multiply(getValorUnitario());
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public Material getMaterial(){
        return itemReqCompra.getMaterial();
    }

    @Override
    public int compareTo(GuiaDistribuicaoItemVO o) {
        try {
            return ComparisonChain.start().compare(getItemReqCompra().getItemContrato().getNumero(), o.getItemReqCompra().getItemContrato().getNumero())
                .compare(getItemReqCompra().getMaterial().getCodigo(), o.getItemReqCompra().getMaterial().getCodigo()).result();
        } catch (Exception e) {
            return 0;
        }
    }
}
