/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoDespesaORCDTO;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author venon
 */
public enum TipoDespesaORC implements EnumComDescricao {

    ORCAMENTARIA("Crédito Orçamentário", TipoDespesaORCDTO.ORCAMENTARIA),
    SUPLEMENTAR("Crédito Suplementar", TipoDespesaORCDTO.SUPLEMENTAR),
    ESPECIAL("Crédito Especial", TipoDespesaORCDTO.ESPECIAL),
    EXTRAORDINARIA("Crédito Extraordinário", TipoDespesaORCDTO.EXTRAORDINARIA);
    private String descricao;
    private TipoDespesaORCDTO toDto;

    TipoDespesaORC(String descricao, TipoDespesaORCDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoDespesaORCDTO getToDto() {
        return toDto;
    }
}
