package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoAjusteBemMovelDTO;

/**
 * Created by mga on 06/03/2018.
 */
public enum TipoAjusteBemMovel {

    ORIGINAL("Original", TipoAjusteBemMovelDTO.ORIGINAL),
    DEPRECIACAO("Depreciação", TipoAjusteBemMovelDTO.DEPRECIACAO),
    AMORTIZACAO("Amortização", TipoAjusteBemMovelDTO.AMORTIZACAO);
    private String descricao;
    private TipoAjusteBemMovelDTO dto;

    TipoAjusteBemMovel(String descricao, TipoAjusteBemMovelDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isOriginal() {
        return ORIGINAL.equals(this);
    }

    public boolean isDepreciacao() {
        return DEPRECIACAO.equals(this);
    }

    public boolean isAmortizacao() {
        return AMORTIZACAO.equals(this);
    }
}
