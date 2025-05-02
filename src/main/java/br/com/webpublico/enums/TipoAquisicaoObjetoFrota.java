package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoAquisicaoObjetoFrotaDTO;

/**
 * Created by mga on 16/04/2018.
 */
public enum TipoAquisicaoObjetoFrota implements EnumComDescricao {
    PROPRIO("Próprio", TipoAquisicaoObjetoFrotaDTO.PROPRIO),
    ALUGADO("Alugado", TipoAquisicaoObjetoFrotaDTO.ALUGADO);
    private String descricao;
    private TipoAquisicaoObjetoFrotaDTO toDto;

    TipoAquisicaoObjetoFrota(String descricao, TipoAquisicaoObjetoFrotaDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoAquisicaoObjetoFrotaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
