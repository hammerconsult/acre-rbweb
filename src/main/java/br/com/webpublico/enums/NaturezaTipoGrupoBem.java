/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.NaturezaTipoGrupoBemDTO;

/**
 * @author Edi
 */
public enum NaturezaTipoGrupoBem {

    ORIGINAL("A - Original", NaturezaTipoGrupoBemDTO.ORIGINAL),
    DEPRECIACAO("B - Depreciação", NaturezaTipoGrupoBemDTO.DEPRECIACAO),
    AMORTIZACAO("C - Amortização", NaturezaTipoGrupoBemDTO.AMORTIZACAO),
    EXAUSTAO("D - Exaustão", NaturezaTipoGrupoBemDTO.EXAUSTAO),
    REDUCAO("E - Redução", NaturezaTipoGrupoBemDTO.REDUCAO),
    VPD("F - VPD", NaturezaTipoGrupoBemDTO.VPD),
    VPA("G - VPA", NaturezaTipoGrupoBemDTO.VPA);
    private String descricao;
    private NaturezaTipoGrupoBemDTO toDto;

    private NaturezaTipoGrupoBem(String descricao, NaturezaTipoGrupoBemDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public NaturezaTipoGrupoBemDTO getToDto() {
        return toDto;
    }

    public boolean isOriginal() {
        return NaturezaTipoGrupoBem.ORIGINAL.equals(this);
    }

    public boolean isVPA() {
        return NaturezaTipoGrupoBem.VPA.equals(this);
    }

    public boolean isVPD() {
        return NaturezaTipoGrupoBem.VPD.equals(this);
    }

    public boolean isAmortizacao() {
        return NaturezaTipoGrupoBem.AMORTIZACAO.equals(this);
    }

    public boolean isExaustao() {
        return NaturezaTipoGrupoBem.EXAUSTAO.equals(this);
    }

    public boolean isDepreciacao() {
        return NaturezaTipoGrupoBem.DEPRECIACAO.equals(this);
    }

    public boolean isReducao() {
        return NaturezaTipoGrupoBem.REDUCAO.equals(this);
    }
}
