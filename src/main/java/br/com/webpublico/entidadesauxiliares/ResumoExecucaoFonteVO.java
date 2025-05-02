package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ResumoExecucaoFonteVO {

    private String fonteDespesaOrc;
    private BigDecimal valorTotal;

    public String getFonteDespesaOrc() {
        return fonteDespesaOrc;
    }

    public void setFonteDespesaOrc(String fonteDespesaOrc) {
        this.fonteDespesaOrc = fonteDespesaOrc;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
