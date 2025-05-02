package br.com.webpublico.enums.administrativo;

import br.com.webpublico.webreportdto.dto.administrativo.TipoManutencaoDTO;

/**
 * Created by zaca on 12/05/17.
 */
public enum TipoManutencao {
    PREVENTIVA("Preventiva", TipoManutencaoDTO.PREVENTIVA),
    CORRETIVA("Corretiva", TipoManutencaoDTO.CORRETIVA);
    private String descricao;
    private TipoManutencaoDTO toDto;

    TipoManutencao(String descricao, TipoManutencaoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoManutencaoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
