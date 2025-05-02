/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoExclusaoMaterial implements EnumComDescricao {

    SAIDA_CONSUMO("Saída por Consumo"),
    ENTRADA_COMPRA("Entrada por Compra"),
    REQUISICAO_MATERIAL_CONSUMO("Requisição de Material por Consumo"),
    AVALIACAO_MATERIAL("Avaliação Material"),
    CONVERSAO_UNIDADE_MEDIDA("Conversão de Unidade Medida");
    private String descricao;

    private TipoExclusaoMaterial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSaidaConsumo(){
        return SAIDA_CONSUMO.equals(this);
    }

    public boolean isEntradaCompra(){
        return ENTRADA_COMPRA.equals(this);
    }

    public boolean isRequisicaoConsumo(){
        return REQUISICAO_MATERIAL_CONSUMO.equals(this);
    }

    public boolean isAvaliacaoMaterial(){
        return AVALIACAO_MATERIAL.equals(this);
    }

    public boolean isConversaoUnidadeMedida(){
        return CONVERSAO_UNIDADE_MEDIDA.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
