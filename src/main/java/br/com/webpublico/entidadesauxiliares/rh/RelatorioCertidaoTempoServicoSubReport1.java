package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioCertidaoTempoServicoSubReport1 {

    private Integer ano;
    private Integer tempoBruto;
    private Integer faltasJustificadas;
    private Integer faltasInjustificadas;
    private Integer licencasComOnus;
    private Integer licencasSemOnus;
    private Integer licencasQueDeduzemDias;
    private String suspensoes;

    public static List<RelatorioCertidaoTempoServicoSubReport1> preencherDados(List<Object[]> objs) {
        List<RelatorioCertidaoTempoServicoSubReport1> retorno = Lists.newArrayList();
        for (Object[] obj : objs) {
            RelatorioCertidaoTempoServicoSubReport1 rel = new RelatorioCertidaoTempoServicoSubReport1();
            rel.setAno(Integer.valueOf(((BigDecimal) obj[0]).toString()));
            rel.setTempoBruto(Integer.valueOf(((BigDecimal) obj[1]).toString()));
            rel.setFaltasJustificadas(Integer.valueOf(((BigDecimal) obj[2]).toString()));
            rel.setFaltasInjustificadas(Integer.valueOf(((BigDecimal) obj[3]).toString()));
            rel.setLicencasComOnus(Integer.valueOf(((BigDecimal) obj[4]).toString()));
            rel.setLicencasSemOnus(Integer.valueOf(((BigDecimal) obj[5]).toString()));
            rel.setLicencasQueDeduzemDias(Integer.valueOf(((BigDecimal) obj[6]).toString()));
            rel.setSuspensoes(String.valueOf(obj[7]));
            retorno.add(rel);
        }
        return retorno;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getTempoBruto() {
        return tempoBruto;
    }

    public void setTempoBruto(Integer tempoBruto) {
        this.tempoBruto = tempoBruto;
    }

    public Integer getFaltasJustificadas() {
        return faltasJustificadas;
    }

    public void setFaltasJustificadas(Integer faltasJustificadas) {
        this.faltasJustificadas = faltasJustificadas;
    }

    public Integer getFaltasInjustificadas() {
        return faltasInjustificadas;
    }

    public void setFaltasInjustificadas(Integer faltasInjustificadas) {
        this.faltasInjustificadas = faltasInjustificadas;
    }

    public Integer getLicencasComOnus() {
        return licencasComOnus;
    }

    public void setLicencasComOnus(Integer licencasComOnus) {
        this.licencasComOnus = licencasComOnus;
    }

    public Integer getLicencasSemOnus() {
        return licencasSemOnus;
    }

    public void setLicencasSemOnus(Integer licencasSemOnus) {
        this.licencasSemOnus = licencasSemOnus;
    }

    public String getSuspensoes() {
        return suspensoes;
    }

    public void setSuspensoes(String suspensoes) {
        this.suspensoes = suspensoes;
    }

    public Integer getLicencasQueDeduzemDias() {
        return licencasQueDeduzemDias;
    }

    public void setLicencasQueDeduzemDias(Integer licencasQueDeduzemDias) {
        this.licencasQueDeduzemDias = licencasQueDeduzemDias;
    }
}
