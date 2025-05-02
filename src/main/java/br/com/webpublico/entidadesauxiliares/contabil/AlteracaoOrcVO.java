package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.FonteDeRecursos;

import java.math.BigDecimal;

public class AlteracaoOrcVO {
    private FonteDeRecursos fonteDeRecursos;
    private BigDecimal valor;
    private Boolean encontrouFonte;

    public AlteracaoOrcVO() {
        encontrouFonte = false;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getEncontrouFonte() {
        return encontrouFonte;
    }

    public void setEncontrouFonte(Boolean encontrouFonte) {
        this.encontrouFonte = encontrouFonte;
    }
}
