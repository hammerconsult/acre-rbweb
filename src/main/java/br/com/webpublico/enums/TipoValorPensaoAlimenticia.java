/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.rh.TipoValorPensaoAlimenticiaDTO;

/**
 * @author andre
 */
public enum TipoValorPensaoAlimenticia implements EnumComDescricao {
    VALOR_FIXO("Valor Fixo", TipoValorPensaoAlimenticiaDTO.VALOR_FIXO),
    PERCENTUAL_SOBRE_SALARIO_MINIMO("Percentual sobre o salário mínimo", TipoValorPensaoAlimenticiaDTO.PERCENTUAL_SOBRE_SALARIO_MINIMO),
    SALARIO_MINIMO_INTEGRAL("Salário Mínimo Integral", TipoValorPensaoAlimenticiaDTO.SALARIO_MINIMO_INTEGRAL),
    BASE("Base", TipoValorPensaoAlimenticiaDTO.BASE),
    BASE_BRUTO("Base Sobre o Valor Bruto", TipoValorPensaoAlimenticiaDTO.BASE_BRUTO),
    BASE_SOBRE_LIQUIDO("Base Sobre o Valor Liquido", TipoValorPensaoAlimenticiaDTO.BASE_SOBRE_LIQUIDO);
    private String descricao;
    private TipoValorPensaoAlimenticiaDTO toDto;

    TipoValorPensaoAlimenticia(String descricao, TipoValorPensaoAlimenticiaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoValorPensaoAlimenticiaDTO getToDto() {
        return toDto;
    }

    public static boolean isTipoBaseOuBaseBruto(TipoValorPensaoAlimenticia tipo) {
        return BASE.equals(tipo) || BASE_BRUTO.equals(tipo) || BASE_SOBRE_LIQUIDO.equals(tipo);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
