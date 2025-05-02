package br.com.webpublico.enums.rh.relatorios;

import br.com.webpublico.webreportdto.dto.rh.TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO;

public enum TipoRelatorioConferenciaConfiguracaoVerbasEsocial {
    VERBA("Verba", TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO.VERBA),
    EMPREGADOR("Empregador", TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO.EMPREGADOR);

    private String descricao;
    private TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO toDto;

    TipoRelatorioConferenciaConfiguracaoVerbasEsocial(String descricao, TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRelatorioConferenciaConfiguracaoVerbasEsocialDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
