package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoRelatorioAnexoDoisPlanejamentoDTO;

public enum TipoRelatorioAnexoDoisPlanejamento {

    GERAL("Geral", TipoRelatorioAnexoDoisPlanejamentoDTO.GERAL),
    ORGAO("Órgão", TipoRelatorioAnexoDoisPlanejamentoDTO.ORGAO),
    ORGAO_UNIDADE("Órgão/Unidade", TipoRelatorioAnexoDoisPlanejamentoDTO.ORGAO_UNIDADE);

    private String descricao;
    private TipoRelatorioAnexoDoisPlanejamentoDTO toDto;

    TipoRelatorioAnexoDoisPlanejamento(String descricao, TipoRelatorioAnexoDoisPlanejamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRelatorioAnexoDoisPlanejamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
