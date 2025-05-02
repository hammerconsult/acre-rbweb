/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoRiscoFiscalLDODTO;

/**
 * @author arthur
 */
public enum TipoRiscoFiscalLDO {
    PASSIVO_CONTINGENTE("Passivo Contingente", TipoRiscoFiscalLDODTO.PASSIVO_CONTINGENTE),
    DEMAIS("Demais", TipoRiscoFiscalLDODTO.DEMAIS);
    private String descricao;
    private TipoRiscoFiscalLDODTO toDto;

    @Override
    public String toString() {
        return descricao;
    }

    TipoRiscoFiscalLDO(String descricao, TipoRiscoFiscalLDODTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRiscoFiscalLDODTO getToDto() {
        return toDto;
    }
}
