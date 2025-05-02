/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoBalanceteDTO;

/**
 * @author major
 */
public enum TipoBalancete {
    TRANSPORTE("Transporte", TipoBalanceteDTO.TRANSPORTE),
    ABERTURA("Abertura", TipoBalanceteDTO.ABERTURA),
    MENSAL("Ordinário", TipoBalanceteDTO.MENSAL),
    APURACAO("Apuração", TipoBalanceteDTO.APURACAO),
    ENCERRAMENTO("Encerramento", TipoBalanceteDTO.ENCERRAMENTO);

    private String descricao;
    private TipoBalanceteDTO toDto;

    TipoBalancete(String descricao, TipoBalanceteDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoBalanceteDTO getToDto() {
        return toDto;
    }
}
