package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ItemComparativoDebitosCalculadosVO {

    private String tributo;
    private BigDecimal valorAnterior;
    private BigDecimal valorAtual;
    private BigDecimal diferenca;
    private BigDecimal percentagem;

    public ItemComparativoDebitosCalculadosVO() {
        this.valorAnterior = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor, boolean atual) {
        if (atual)
            this.valorAtual = valor;
        else
            this.valorAnterior = valor;
    }

    public BigDecimal getValor(boolean atual) {
        if (atual)
            return valorAtual;
        return valorAnterior;
    }

    public String getTributo() {
        return tributo;
    }

    public void setTributo(String tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(BigDecimal valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getPercentagem() {
        return percentagem;
    }

    public void setPercentagem(BigDecimal percentagem) {
        this.percentagem = percentagem;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemComparativoDebitosCalculadosVO other = (ItemComparativoDebitosCalculadosVO) obj;
        return getTributo().equals(other.getTributo());
    }
}
