/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoEstoqueDTO;

/**
 * @author major
 */
public enum TipoEstoque implements EnumComDescricao {
    PRODUTOS_ACABADOS_PRINCIPAL("Produtos Acabados Principal", TipoEstoqueDTO.PRODUTOS_ACABADOS_PRINCIPAL),
    SERVIÇOS_ACABADOS_PRINCIPAL("Serviços Acabados Principal", TipoEstoqueDTO.SERVIÇOS_ACABADOS_PRINCIPAL),
    BEM_ESTOQUE_INTEGRACAO("Bem Estoque Integração", TipoEstoqueDTO.BEM_ESTOQUE_INTEGRACAO),
    PRODUTOS_ELABORACAO_PRINCIPAL("Produtos em Elaboração Principal", TipoEstoqueDTO.PRODUTOS_ELABORACAO_PRINCIPAL),
    SERVICOS_ELABORACAO_PRINCIPAL("Serviços em Elaboração Principal", TipoEstoqueDTO.SERVICOS_ELABORACAO_PRINCIPAL),
    MATERIA_PRIMA_PRINCIPAL("Matéria-prima Principal", TipoEstoqueDTO.MATERIA_PRIMA_PRINCIPAL),
    MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO("Material de Consumo Principal - Almoxarifado", TipoEstoqueDTO.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO),
    ESTOQUE_DIVERSOS_OUTROS_ESTOQUE("Estoques Diversos - Outros Estoques", TipoEstoqueDTO.ESTOQUE_DIVERSOS_OUTROS_ESTOQUE);
    private String descricao;
    private TipoEstoqueDTO toDto;

    TipoEstoque(String descricao, TipoEstoqueDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoEstoqueDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
