package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.DeducaoHabitese;

import java.math.BigDecimal;

public class WsDeducaoHabitese {

    private String descricao;
    private BigDecimal baseDeCalculo;

    public WsDeducaoHabitese() {
    }

    public WsDeducaoHabitese(DeducaoHabitese deducaoHabitese) {
        this.descricao = deducaoHabitese.getDescricao();
        this.baseDeCalculo = deducaoHabitese.getBaseDeCalculo();
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
