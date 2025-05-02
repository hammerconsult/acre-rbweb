package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LocalEstoqueOrcamentario;
import br.com.webpublico.entidades.Material;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class CardapioSaidaMaterialItemVO {

    private Material material;
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;
    private BigDecimal quantidadeGuia;
    private BigDecimal quantidadeSaida;
    private BigDecimal quantidadeOutraSaida;
    private BigDecimal quantidadeEstoque;
    private BigDecimal valorEstoque;

    public CardapioSaidaMaterialItemVO() {
        quantidadeSaida = BigDecimal.ZERO;
        quantidadeGuia = BigDecimal.ZERO;
        quantidadeOutraSaida = BigDecimal.ZERO;
        quantidadeEstoque = BigDecimal.ZERO;
        valorEstoque = BigDecimal.ZERO;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BigDecimal getQuantidadeSaida() {
        return quantidadeSaida;
    }

    public void setQuantidadeSaida(BigDecimal quantidadeSaida) {
        this.quantidadeSaida = quantidadeSaida;
    }

    public BigDecimal getQuantidadeGuia() {
        return quantidadeGuia;
    }

    public void setQuantidadeGuia(BigDecimal quantidadeGuia) {
        this.quantidadeGuia = quantidadeGuia;
    }

    public boolean hasQuantidadeSaida() {
        return quantidadeSaida != null && quantidadeSaida.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public BigDecimal getValorEstoque() {
        return valorEstoque;
    }

    public void setValorEstoque(BigDecimal valorEstoque) {
        this.valorEstoque = valorEstoque;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public BigDecimal getQuantidadeOutraSaida() {
        return quantidadeOutraSaida;
    }

    public void setQuantidadeOutraSaida(BigDecimal quantidadeOutraSaida) {
        this.quantidadeOutraSaida = quantidadeOutraSaida;
    }

    public BigDecimal getQuantidadeDisponivel() {
        try {
            return getQuantidadeGuia().subtract(getQuantidadeOutraSaida()).subtract(getQuantidadeSaida());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotal() {
        if (hasQuantidadeSaida()) {
            BigDecimal valorTotal = getQuantidadeSaida().multiply(getValorUnitario());
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorUnitario() {
        try {
            return getValorEstoque().divide(getQuantidadeEstoque(), 8, RoundingMode.HALF_EVEN).setScale(4, RoundingMode.HALF_EVEN);
        } catch (ArithmeticException a) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardapioSaidaMaterialItemVO that = (CardapioSaidaMaterialItemVO) o;
        return Objects.equals(material, that.material) && Objects.equals(localEstoqueOrcamentario, that.localEstoqueOrcamentario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, localEstoqueOrcamentario);
    }
}
