/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.rh.TipoCedenciaContratoFPDTO;

/**
 * @author andre
 */
@GrupoDiagrama(nome = "RecursosHumanos")
public enum TipoCedenciaContratoFP {
    COM_ONUS("Com Ônus", TipoCedenciaContratoFPDTO.COM_ONUS),
    SEM_ONUS("Sem Ônus", TipoCedenciaContratoFPDTO.SEM_ONUS);

    private String descricao;

    private TipoCedenciaContratoFPDTO toDto;

    TipoCedenciaContratoFP(String descricao, TipoCedenciaContratoFPDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCedenciaContratoFPDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
