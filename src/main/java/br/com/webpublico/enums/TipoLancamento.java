/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.comum.TipoLancamentoDTO;

/**
 * @author Claudio
 */
public enum TipoLancamento {
    NORMAL("Normal", TipoLancamentoDTO.NORMAL, "N"),
    ESTORNO("Estorno", TipoLancamentoDTO.ESTORNO, "E");

    private String descricao;
    private TipoLancamentoDTO toDto;
    private String abreviacao;

    TipoLancamento(String descricao, TipoLancamentoDTO toDto, String abreviacao) {
        this.descricao = descricao;
        this.toDto = toDto;
        this.abreviacao = abreviacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public boolean isNormal() {
        return NORMAL.equals(this);
    }

    public boolean isEstorno() {
        return ESTORNO.equals(this);
    }

    public TipoLancamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
