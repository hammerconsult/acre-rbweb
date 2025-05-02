package br.com.webpublico.enums.rh.esocial;

import br.com.webpublico.webreportdto.dto.rh.TipoApuracaoFolhaDTO;

public enum TipoApuracaoFolha {
    MENSAL("Mensal", TipoApuracaoFolhaDTO.MENSAL),
    SALARIO_13("Décimo Terceiro", TipoApuracaoFolhaDTO.SALARIO_13);

    private String descricao;
    private TipoApuracaoFolhaDTO tipoApuracaoFolhaDTO;

    TipoApuracaoFolha(String descricao, TipoApuracaoFolhaDTO tipoApuracaoFolhaDTO) {
        this.descricao = descricao;
        this.tipoApuracaoFolhaDTO = tipoApuracaoFolhaDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoApuracaoFolhaDTO getToDTO() {
        return tipoApuracaoFolhaDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
