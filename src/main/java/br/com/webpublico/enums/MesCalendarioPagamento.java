/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.MesCalendarioPagamentoDTO;

/**
 * @author andre
 */
public enum MesCalendarioPagamento {
    JANEIRO(1, "Janeiro", MesCalendarioPagamentoDTO.JANEIRO),
    FEVEREIRO(2, "Fevereiro", MesCalendarioPagamentoDTO.FEVEREIRO),
    MARCO(3, "Março", MesCalendarioPagamentoDTO.MARCO),
    ABRIL(4, "Abril", MesCalendarioPagamentoDTO.ABRIL),
    MAIO(5, "Maio", MesCalendarioPagamentoDTO.MAIO),
    JUNHO(6, "Junho", MesCalendarioPagamentoDTO.JUNHO),
    JULHO(7, "Julho", MesCalendarioPagamentoDTO.JULHO),
    AGOSTO(8, "Agosto", MesCalendarioPagamentoDTO.AGOSTO),
    SETEMBRO(9, "Setembro", MesCalendarioPagamentoDTO.SETEMBRO),
    OUTUBRO(10, "Outubro", MesCalendarioPagamentoDTO.OUTUBRO),
    NOVEMBRO(11, "Novembro", MesCalendarioPagamentoDTO.NOVEMBRO),
    DEZEMBRO(12, "Dezembro", MesCalendarioPagamentoDTO.DEZEMBRO),
    DECIMO(13, "Décimo", MesCalendarioPagamentoDTO.DECIMO);

    private String descricao;
    private Integer numeroDoMes;
    private MesCalendarioPagamentoDTO toDto;

    MesCalendarioPagamento(Integer numeroDoMes, String descricao, MesCalendarioPagamentoDTO toDto) {
        this.numeroDoMes = numeroDoMes;
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNumeroDoMes() {
        return numeroDoMes;
    }

    public void setNumeroDoMes(Integer numeroDoMes) {
        this.numeroDoMes = numeroDoMes;
    }

    public MesCalendarioPagamentoDTO getToDto() {
        return toDto;
    }

    public static MesCalendarioPagamento getMesToInt(int mes) {
        switch (mes) {

            case 1:
                return MesCalendarioPagamento.JANEIRO;
            case 2:
                return MesCalendarioPagamento.FEVEREIRO;
            case 3:
                return MesCalendarioPagamento.MARCO;
            case 4:
                return MesCalendarioPagamento.ABRIL;
            case 5:
                return MesCalendarioPagamento.MAIO;
            case 6:
                return MesCalendarioPagamento.JUNHO;
            case 7:
                return MesCalendarioPagamento.JULHO;
            case 8:
                return MesCalendarioPagamento.AGOSTO;
            case 9:
                return MesCalendarioPagamento.SETEMBRO;
            case 10:
                return MesCalendarioPagamento.OUTUBRO;
            case 11:
                return MesCalendarioPagamento.NOVEMBRO;
            case 12:
                return MesCalendarioPagamento.DEZEMBRO;
            case 13:
                return MesCalendarioPagamento.DECIMO;
            default:
                return null;
        }
    }

    public String getNumeroMesString() {
        String s = numeroDoMes + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }
}
