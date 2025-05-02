package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DeducaoHabitese;

import java.math.BigDecimal;

public class VODeducaoHabitese {

    private String descricao;
    private BigDecimal baseDeCalculo;

    public VODeducaoHabitese(DeducaoHabitese deducoe) {
        this.descricao = deducoe.getDescricao();
        this.baseDeCalculo = deducoe.getBaseDeCalculo();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getBaseDeCalculo() {
        return baseDeCalculo;
    }

    public void setBaseDeCalculo(BigDecimal baseDeCalculo) {
        this.baseDeCalculo = baseDeCalculo;
    }
}
