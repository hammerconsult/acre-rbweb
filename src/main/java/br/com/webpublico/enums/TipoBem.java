/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoBemDTO;

/**
 * @author Claudio
 */
public enum TipoBem {
    ESTOQUE("Estoque", "", TipoBemDTO.ESTOQUE),
    MOVEIS("Móveis", "Móvel", TipoBemDTO.MOVEIS),
    IMOVEIS("Imóveis", "Imóvel", TipoBemDTO.IMOVEIS),
    INTANGIVEIS("Intangíveis", "Intangível", TipoBemDTO.INTANGIVEIS),
    NAO_APLICAVEL("Não Aplicável", "Não Aplicáveis", TipoBemDTO.NAO_APLICAVEL);
    private String descricao;
    private String singular;
    private TipoBemDTO toDto;

    TipoBem(String descricao, String singular, TipoBemDTO toDto) {
        this.descricao = descricao;
        this.singular = singular;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSingular() {
        return singular;
    }

    public TipoBemDTO getToDto() {
        return toDto;
    }

    public boolean isMovel() {
        return MOVEIS.equals(this);
    }

    public boolean isImovel() {
        return IMOVEIS.equals(this);
    }

    public boolean isIntangivel() {
        return INTANGIVEIS.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
