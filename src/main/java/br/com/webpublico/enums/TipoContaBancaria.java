/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoContaBancariaDTO;

/**
 * @author fabio
 */
public enum TipoContaBancaria {

    APLICACAO_CORRENTE("Conta Aplicação Corrente", TipoContaBancariaDTO.APLICACAO_CORRENTE),
    APLICACAO_VINCULADA("Conta Aplicação Vinculada", TipoContaBancariaDTO.APLICACAO_VINCULADA),
    CORRENTE("Conta Corrente", TipoContaBancariaDTO.CORRENTE),
    POUPANCA("Conta Poupança", TipoContaBancariaDTO.POUPANCA),
    SALARIO("Conta Salário", TipoContaBancariaDTO.SALARIO),
    VINCULADA("Conta Vinculada", TipoContaBancariaDTO.VINCULADA);

    private String descricao;
    private TipoContaBancariaDTO toDto;

    private TipoContaBancaria(String descricao, TipoContaBancariaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoContaBancariaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
