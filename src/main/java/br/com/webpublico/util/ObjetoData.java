/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 * @author peixe
 */
public class ObjetoData {

    private Integer anos;
    private Integer meses;
    private Integer dias;

    public ObjetoData(Integer ano, Integer mes, Integer dia) {
        this.anos = ano;
        this.meses = mes;
        this.dias = dia;
    }

    public ObjetoData() {
    }

    public Integer getAnos() {
        return anos;
    }

    public void setAnos(Integer anos) {
        this.anos = anos;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    public String toStringFormatadoSiprev() {
        return StringUtil.cortaOuCompletaEsquerda(anos.toString(), 2, "0") + "/" + StringUtil.cortaOuCompletaEsquerda(meses.toString(), 2, "0") + "/" + StringUtil.cortaOuCompletaEsquerda(dias.toString(), 2, "0");
    }

    public void addDias(Integer dias) {
        this.dias += dias;
    }

    public void addMeses(Integer meses) {
        this.meses += meses;
    }

    public void addAnos(Integer anos) {
        this.anos += anos;
    }

    public void subtractDias(Integer dias) {
        this.dias -= dias;
    }

    public void subtractMeses(Integer meses) {
        this.meses -= meses;
    }

    public void subtractAnos(Integer anos) {
        this.anos -= anos;
    }

    public boolean temDias() {
        return dias > 0;
    }

    public boolean temMeses() {
        return meses > 0;
    }
}
