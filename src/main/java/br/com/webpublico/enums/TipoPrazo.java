/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.webreportdto.dto.administrativo.TipoPrazoDTO;

import java.util.Calendar;

/**
 * @author lucas
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoPrazo {

    SEGUNDOS("Segundos", TipoPrazoDTO.SEGUNDOS),
    MINUTOS("Minutos", TipoPrazoDTO.MINUTOS),
    HORAS("Horas", TipoPrazoDTO.HORAS),
    DIAS("Dias", TipoPrazoDTO.DIAS),
    SEMANAS("Semanas", TipoPrazoDTO.SEMANAS),
    MESES("Meses", TipoPrazoDTO.MESES),
    ANOS("Anos", TipoPrazoDTO.ANOS);


    private String descricao;

    private TipoPrazoDTO dto;

    private TipoPrazo(String descricao, TipoPrazoDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoPrazoDTO getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public int getCalendarCorrespondente() {
        switch (this) {
            case SEGUNDOS:
                return Calendar.SECOND;
            case MINUTOS:
                return Calendar.MINUTE;
            case HORAS:
                return Calendar.HOUR;
            case DIAS:
                return Calendar.DAY_OF_MONTH;
            case SEMANAS:
                return Calendar.WEEK_OF_MONTH;
            case MESES:
                return Calendar.MONTH;
            case ANOS:
                return Calendar.YEAR;
        }
        return 0;
    }
}
