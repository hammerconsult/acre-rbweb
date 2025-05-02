package br.com.webpublico.enums.administrativo;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoExibicaoRelatorioRomaneioDTO;

public enum TipoExibicaoRelatorioRomaneio implements EnumComDescricao {
    FEIRA("Individualizar por Feira", TipoExibicaoRelatorioRomaneioDTO.FEIRA),
    FEIRANTE("Individualizar por Feirante", TipoExibicaoRelatorioRomaneioDTO.FEIRANTE);
    private String descricao;
    private TipoExibicaoRelatorioRomaneioDTO toDto;

    TipoExibicaoRelatorioRomaneio(String descricao, TipoExibicaoRelatorioRomaneioDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoExibicaoRelatorioRomaneioDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }

    public boolean isFeirante() {
        return TipoExibicaoRelatorioRomaneio.FEIRANTE.equals(this);
    }
}
