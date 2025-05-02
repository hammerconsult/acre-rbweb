package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoEmpenhoEstornoDTO;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 07/01/15
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public enum TipoEmpenhoEstorno {
    CANCELAMENTO("Cancelamento",TipoEmpenhoEstornoDTO.CANCELAMENTO),
    PRESCRICAO("Prescrição", TipoEmpenhoEstornoDTO.PRESCRICAO);
    private String descricao;
    private TipoEmpenhoEstornoDTO toDto;

    TipoEmpenhoEstorno(String descricao, TipoEmpenhoEstornoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoEmpenhoEstornoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
