/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Heinz
 */
public enum TipoBaseCalculo implements EnumComDescricao {
    VALOR_NEGOCIADO("Valor Negociado"),
    VALOR_VENAL("Valor Venal do Imóvel"),
    VALOR_VENAL_ACRESCIMOS("Valor Venal do Imóvel + Percentual de Reajuste"),
    VALOR_APURADO("Valor Avaliado");

    private final String descricao;

    TipoBaseCalculo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isValorVenal() {
        return this.equals(VALOR_VENAL) || this.equals(VALOR_VENAL_ACRESCIMOS);
    }
}
