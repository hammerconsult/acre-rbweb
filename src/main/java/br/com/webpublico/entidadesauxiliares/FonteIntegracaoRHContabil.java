package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FonteDeRecursos;

import java.math.BigDecimal;

public class FonteIntegracaoRHContabil {

    private FonteDeRecursos fonteDeRecursos;
    private BigDecimal valorBruto;
    private BigDecimal valorRetencao;
    private BigDecimal valorLiquido;


    public FonteIntegracaoRHContabil() {
        this.valorBruto = BigDecimal.ZERO;
        this.valorRetencao = BigDecimal.ZERO;
        this.valorLiquido = BigDecimal.ZERO;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(BigDecimal valorBruto) {
        this.valorBruto = valorBruto;
    }

    public BigDecimal getValorRetencao() {
        return valorRetencao;
    }

    public void setValorRetencao(BigDecimal valorRetencao) {
        this.valorRetencao = valorRetencao;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }
}
