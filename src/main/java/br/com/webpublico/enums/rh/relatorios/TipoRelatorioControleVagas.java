package br.com.webpublico.enums.rh.relatorios;

import br.com.webpublico.webreportdto.dto.rh.TipoRelatorioControleVagasDTO;

/**
 * Created by peixe on 26/06/17.
 */
public enum TipoRelatorioControleVagas {
    ORGAO_ENTIDADE("Órgão", TipoRelatorioControleVagasDTO.ORGAO_ENTIDADE),
    UNIDADEADM("Unidade Administrativa", TipoRelatorioControleVagasDTO.UNIDADEADM),
    CARGO_FUNCAO("Cargo/Função", TipoRelatorioControleVagasDTO.CARGO_FUNCAO);

    private String descricao;
    private TipoRelatorioControleVagasDTO toDto;

    TipoRelatorioControleVagas(String descricao, TipoRelatorioControleVagasDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public TipoRelatorioControleVagasDTO getToDto() {
        return toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
