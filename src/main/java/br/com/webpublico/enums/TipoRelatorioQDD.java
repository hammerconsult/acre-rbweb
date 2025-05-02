package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoRelatorioQDDDTO;

public enum TipoRelatorioQDD {

    TOTAL_FONTE_RECURSOS("Total por Fonte de Recursos", TipoRelatorioQDDDTO.TOTAL_FONTE_RECURSOS),
    CONTAS_FONTES_RECURSOS("Contas e Fontes de Recursos", TipoRelatorioQDDDTO.CONTAS_FONTES_RECURSOS);

    private String descricao;
    private TipoRelatorioQDDDTO toDto;

    TipoRelatorioQDD(String descricao, TipoRelatorioQDDDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRelatorioQDDDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
