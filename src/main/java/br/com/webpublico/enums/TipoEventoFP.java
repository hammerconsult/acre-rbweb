/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.TipoEventoFPDTO;

import java.math.BigDecimal;

/**
 * @author Arthur
 */
public enum TipoEventoFP {
    VANTAGEM("Vencimento, provento ou pensão", 1, TipoEventoFPDTO.VANTAGEM),
    DESCONTO("Desconto", 2, TipoEventoFPDTO.DESCONTO),
    INFORMATIVO("Informativo", 3, TipoEventoFPDTO.INFORMATIVO),
    INFORMATIVO_DEDUTORA("Informativa dedutora", 4, TipoEventoFPDTO.INFORMATIVO_DEDUTORA);
    private String descricao;

    private Integer codigoEsocial;
    private TipoEventoFPDTO toDto;

    private TipoEventoFP(String descricao, Integer codigoEsocial, TipoEventoFPDTO toDto) {
        this.descricao = descricao;
        this.codigoEsocial = codigoEsocial;
        this.toDto = toDto;
    }

    public static TipoEventoFP getTipoEventoFPPorValor(TipoEventoFP tipoEventoFP, BigDecimal valor) {
        if (valor.doubleValue() >= 0) {
            return tipoEventoFP;
        }
        if (tipoEventoFP.equals(TipoEventoFP.VANTAGEM) && valor.doubleValue() < 0) {
            return TipoEventoFP.DESCONTO;
        }
        if (tipoEventoFP.equals(TipoEventoFP.DESCONTO) && valor.doubleValue() < 0) {
            return TipoEventoFP.VANTAGEM;
        }
//
//        if (valor.compareTo(new BigDecimal("0")) > 0) {
//            return TipoEventoFP.VANTAGEM;
//        } else {
//            return TipoEventoFP.DESCONTO;
//        }
        return tipoEventoFP;
    }

    public Integer getCodigoEsocial() {
        return codigoEsocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoEventoFPDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
