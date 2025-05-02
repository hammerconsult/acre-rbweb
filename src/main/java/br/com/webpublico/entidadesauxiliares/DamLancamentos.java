package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class DamLancamentos implements Serializable {
    private Long codigoTributo;
    private String descricaoTributo;
    private BigDecimal valorTributo;

    public DamLancamentos() {
        this.valorTributo = BigDecimal.ZERO;
    }

    public Long getCodigoTributo() {
        return codigoTributo;
    }

    public void setCodigoTributo(Long codigoTributo) {
        this.codigoTributo = codigoTributo;
    }

    public String getDescricaoTributo() {
        return descricaoTributo;
    }

    public void setDescricaoTributo(String descricaoTributo) {
        this.descricaoTributo = descricaoTributo;
    }

    public BigDecimal getValorTributo() {
        return valorTributo;
    }

    public void setValorTributo(BigDecimal valorTributo) {
        this.valorTributo = valorTributo;
    }
}
