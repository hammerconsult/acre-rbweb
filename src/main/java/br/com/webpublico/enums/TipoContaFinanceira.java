/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.contabil.TipoContaFinanceiraDTO;

/**
 * @author major
 */
public enum TipoContaFinanceira implements EnumComDescricao {
    ARRECADACAO("Arrecadação", TipoContaFinanceiraDTO.ARRECADACAO),
    CONTA_UNICA("Conta Única", TipoContaFinanceiraDTO.CONTA_UNICA),
    PAGAMENTO("Pagamento", TipoContaFinanceiraDTO.PAGAMENTO),
    MOVIMENTO("Movimento", TipoContaFinanceiraDTO.MOVIMENTO),
    APLICACAO("Aplicação", TipoContaFinanceiraDTO.APLICACAO);
    private String descricao;
    private TipoContaFinanceiraDTO toDto;

    TipoContaFinanceira(String descricao, TipoContaFinanceiraDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoContaFinanceiraDTO getToDto() {
        return toDto;
    }


    @Override
    public String toString() {
        return descricao;
    }
}
