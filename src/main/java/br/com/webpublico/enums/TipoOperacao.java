/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoOperacaoDTO;

/**
 * @author terminal4
 */
public enum TipoOperacao {
    DEBITO("Débito", TipoOperacaoDTO.DEBITO),
    CREDITO("Crédito", TipoOperacaoDTO.CREDITO),
    NENHUMA_OPERACAO("Nenhuma Operação", TipoOperacaoDTO.NENHUMA_OPERACAO);
    private String descricao;
    private TipoOperacaoDTO toDto;

    TipoOperacao(String descricao, TipoOperacaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public boolean isCredito(){
        return CREDITO.equals(this);
    }

    public boolean isDebito(){
        return DEBITO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoOperacaoDTO getToDto() {
        return toDto;
    }
}
