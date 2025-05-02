/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoRestosProcessadoDTO;

/**
 *
 * @author Edi
 */
public enum TipoRestosProcessado {

    PROCESSADOS("Processados", TipoRestosProcessadoDTO.PROCESSADOS),
    NAO_PROCESSADOS("Não Processados", TipoRestosProcessadoDTO.NAO_PROCESSADOS);

    private String descricao;
    private TipoRestosProcessadoDTO toDto;

    TipoRestosProcessado(String descricao, TipoRestosProcessadoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRestosProcessadoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
