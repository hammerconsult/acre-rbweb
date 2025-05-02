/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.ResultanteIndependenteDTO;

/**
 * @author juggernaut
 */
public enum ResultanteIndependente implements EnumComDescricao {

    RESULTANTE_EXECUCAO_ORCAMENTARIA("Resultante da execução orçamentária", ResultanteIndependenteDTO.RESULTANTE_EXECUCAO_ORCAMENTARIA),
    INDEPENDENTE_EXECUCAO_ORCAMENTARIA("Independente da execução orçamentária", ResultanteIndependenteDTO.INDEPENDENTE_EXECUCAO_ORCAMENTARIA);

    private String descricao;
    private ResultanteIndependenteDTO toDto;

    ResultanteIndependente(String descricao, ResultanteIndependenteDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public ResultanteIndependenteDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
