package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class ItemSaidaMaterialVO implements Comparable<ItemSaidaMaterialVO> {

    public static final String MASCARA_DECIMAL_OITO_DIGITOS = "#,########0.00000000";
    private Integer numero;
    private Material material;
    private LocalEstoque localEstoque;
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;
    private HierarquiaOrganizacional hierarquiaAdm;
    private HierarquiaOrganizacional hierarquiaOrc;
    private LoteMaterial loteMaterial;
    private Boolean isUltimoMovimentoEstoque;
    private BigDecimal quantidade;
    private BigDecimal quantidadeEstoqueAtual;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorEstoqueAtual;
    private String observacao;
    private ItemSaidaMaterial itemSaidaMaterial;
    private ItemSaidaMaterialRequisicaoVO itemSaidaMaterialAvaliacao;
    private List<FormularioLoteMaterial> lotesMateriais;

    public ItemSaidaMaterialVO() {
        lotesMateriais = Lists.newArrayList();
        quantidade = BigDecimal.ZERO;
    }

    public List<FormularioLoteMaterial> getLotesMateriais() {
        return lotesMateriais;
    }

    public void setLotesMateriais(List<FormularioLoteMaterial> lotesMateriais) {
        this.lotesMateriais = lotesMateriais;
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemSaidaMaterial;
    }

    public void setItemSaidaMaterial(ItemSaidaMaterial itemSaidaMaterial) {
        this.itemSaidaMaterial = itemSaidaMaterial;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public Boolean getControleLote() {
        return material != null && material.getControleDeLote();
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public HierarquiaOrganizacional getHierarquiaAdm() {
        return hierarquiaAdm;
    }

    public void setHierarquiaAdm(HierarquiaOrganizacional hierarquiaAdm) {
        this.hierarquiaAdm = hierarquiaAdm;
    }

    public HierarquiaOrganizacional getHierarquiaOrc() {
        return hierarquiaOrc;
    }

    public void setHierarquiaOrc(HierarquiaOrganizacional hierarquiaOrc) {
        this.hierarquiaOrc = hierarquiaOrc;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getQuantidadeEstoqueAtual() {
        return quantidadeEstoqueAtual;
    }

    public void setQuantidadeEstoqueAtual(BigDecimal quantidadeEstoqueAtual) {
        this.quantidadeEstoqueAtual = quantidadeEstoqueAtual;
    }

    public BigDecimal getValorEstoqueAtual() {
        return valorEstoqueAtual;
    }

    public void setValorEstoqueAtual(BigDecimal valorEstoqueAtual) {
        this.valorEstoqueAtual = valorEstoqueAtual;
    }

    public ItemSaidaMaterialRequisicaoVO getItemSaidaMaterialAvaliacao() {
        return itemSaidaMaterialAvaliacao;
    }

    public void setItemSaidaMaterialAvaliacao(ItemSaidaMaterialRequisicaoVO itemSaidaMaterialAvaliacao) {
        this.itemSaidaMaterialAvaliacao = itemSaidaMaterialAvaliacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public boolean getDiferencaFinanceiroEstoqueNoUltimoMovimento() {
        if (valorTotal == null || quantidadeEstoqueAtual == null) return false;
        return getQuantidadeEstoqueAtualizado().compareTo(BigDecimal.ZERO) == 0 && getValorEstoqueAtualizado().compareTo(BigDecimal.ZERO) != 0;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public void calcularValorTotal() {
        if (getQuantidade() != null && getValorUnitario() != null) {
            BigDecimal valorTotal = getQuantidade().multiply(getValorUnitario());
            this.valorTotal = valorTotal.setScale(2, RoundingMode.FLOOR);
            setUltimoMovimentoEstoque(getDiferencaFinanceiroEstoqueNoUltimoMovimento());

            if (getUltimoMovimentoEstoque()) {
                setValorTotal(getValorEstoqueAtual());
                BigDecimal valorUnitarioConsolidado = getValorEstoqueAtual().divide(getQuantidade(), 8, RoundingMode.HALF_EVEN);
                setValorUnitario(valorUnitarioConsolidado);
            }
        }
    }

    public String getValorUnitarioDecimal() {
        if (valorUnitario != null) {
            return Util.formatarValor(valorUnitario, MASCARA_DECIMAL_OITO_DIGITOS, false);
        }
        return "";
    }

    public BigDecimal getValorEstoqueAtualizado() {
        if (valorEstoqueAtual == null || getValorTotal() == null) return BigDecimal.ZERO;
        return valorEstoqueAtual.subtract(getValorTotal());
    }

    public BigDecimal getQuantidadeEstoqueAtualizado() {
        if (quantidadeEstoqueAtual == null || quantidade == null) return BigDecimal.ZERO;
        return quantidadeEstoqueAtual.subtract(quantidade);
    }

    public Boolean getUltimoMovimentoEstoque() {
        return isUltimoMovimentoEstoque;
    }

    public void setUltimoMovimentoEstoque(Boolean ultimoMovimentoEstoque) {
        isUltimoMovimentoEstoque = ultimoMovimentoEstoque;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSaidaMaterialVO that = (ItemSaidaMaterialVO) o;
        return Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material);
    }

    @Override
    public int compareTo(ItemSaidaMaterialVO o) {
        if (o.getMaterial() != null
            && !Util.isStringNulaOuVazia(o.getMaterial().getDescricao())
            && getMaterial() != null && !Util.isStringNulaOuVazia(getMaterial().getDescricao())) {
            return StringUtil.removeAcentuacao(getMaterial().getDescricao()).compareToIgnoreCase(StringUtil.removeAcentuacao(o.getMaterial().getDescricao()));
        }
        return 0;
    }
}
