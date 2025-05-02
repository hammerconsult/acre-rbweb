/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.util.StringUtil;
import br.com.webpublico.webreportdto.dto.comum.MesDTO;
import com.google.common.collect.Lists;

import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * @author andre
 */
public enum Mes {

    JANEIRO("Janeiro", 1, MesDTO.JANEIRO),
    FEVEREIRO("Fevereiro", 2, MesDTO.FEVEREIRO),
    MARCO("Março", 3, MesDTO.MARCO),
    ABRIL("Abril", 4, MesDTO.ABRIL),
    MAIO("Maio", 5, MesDTO.MAIO),
    JUNHO("Junho", 6, MesDTO.JUNHO),
    JULHO("Julho", 7, MesDTO.JULHO),
    AGOSTO("Agosto", 8, MesDTO.AGOSTO),
    SETEMBRO("Setembro", 9, MesDTO.SETEMBRO),
    OUTUBRO("Outubro", 10, MesDTO.OUTUBRO),
    NOVEMBRO("Novembro", 11, MesDTO.NOVEMBRO),
    DEZEMBRO("Dezembro", 12, MesDTO.DEZEMBRO);

    private String descricao;
    private int numeroMes;
    private MesDTO toDto;

    Mes(String descricao, int numeroMes, MesDTO toDto) {
        this.descricao = descricao;
        this.numeroMes = numeroMes;
        this.toDto = toDto;
    }

    public MesDTO getToDto() {
        return toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getNumeroMes() {
        return numeroMes;
    }

    public int getNumeroMesIniciandoEmZero() {
        return numeroMes - 1;
    }

    public String getNumeroMesString() {
        return StringUtil.preencheString(String.valueOf(numeroMes), 2, '0');
    }

    public void setNumeroMes(int numeroMes) {
        this.numeroMes = numeroMes;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static Mes getMesToInt(int mes) {
        switch (mes) {

            case 1:
                return Mes.JANEIRO;
            case 2:
                return Mes.FEVEREIRO;
            case 3:
                return Mes.MARCO;
            case 4:
                return Mes.ABRIL;
            case 5:
                return Mes.MAIO;
            case 6:
                return Mes.JUNHO;
            case 7:
                return Mes.JULHO;
            case 8:
                return Mes.AGOSTO;
            case 9:
                return Mes.SETEMBRO;
            case 10:
                return Mes.OUTUBRO;
            case 11:
                return Mes.NOVEMBRO;
            case 12:
                return Mes.DEZEMBRO;
            default:
                return null;
        }
    }

    public static List<String> getTodosMesesComoStringNoIntevalo(Mes inicio, Mes fim) {
        List<String> meses = Lists.newArrayList();
        for (Mes mes : Mes.values()) {
            if (hasMesNoIntervalo(inicio, fim, mes)) {
                meses.add(mes.name());
            }
        }
        return meses;
    }

    private static boolean hasMesNoIntervalo(Mes inicio, Mes fim, Mes mes) {
        return mes.getNumeroMes() >= inicio.getNumeroMes() && mes.getNumeroMes() <= fim.getNumeroMes();
    }

    public static List<Mes> getTodosMesesNoIntevalo(Mes inicio, Mes fim) {
        List<Mes> meses = Lists.newArrayList();
        for (Mes mes : Mes.values()) {
            if (hasMesNoIntervalo(inicio, fim, mes)) {
                meses.add(mes);
            }
        }
        return meses;
    }

    @FacesConverter(value = "mesEnumConverter")
    public static class EnumConverter extends javax.faces.convert.EnumConverter {
        public EnumConverter() {
            super(Mes.class);
        }
    }
}
