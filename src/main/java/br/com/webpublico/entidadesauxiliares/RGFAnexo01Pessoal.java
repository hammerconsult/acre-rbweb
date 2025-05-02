package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by mateus on 23/04/18.
 */
public class RGFAnexo01Pessoal {
    private String descricao;
    private BigDecimal mesMenosOnze;
    private BigDecimal mesMenosDez;
    private BigDecimal mesMenosNove;
    private BigDecimal mesMenosOito;
    private BigDecimal mesMenosSete;
    private BigDecimal mesMenosSeis;
    private BigDecimal mesMenosCinco;
    private BigDecimal mesMenosQuatro;
    private BigDecimal mesMenosTres;
    private BigDecimal mesMenosDois;
    private BigDecimal mesMenosUm;
    private BigDecimal mesAtual;
    private BigDecimal totalUltimosMeses;
    private BigDecimal inscritasRestos;

    public RGFAnexo01Pessoal() {
        inscritasRestos = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getMesMenosOnze() {
        return mesMenosOnze;
    }

    public void setMesMenosOnze(BigDecimal mesMenosOnze) {
        this.mesMenosOnze = mesMenosOnze;
    }

    public BigDecimal getMesMenosDez() {
        return mesMenosDez;
    }

    public void setMesMenosDez(BigDecimal mesMenosDez) {
        this.mesMenosDez = mesMenosDez;
    }

    public BigDecimal getMesMenosNove() {
        return mesMenosNove;
    }

    public void setMesMenosNove(BigDecimal mesMenosNove) {
        this.mesMenosNove = mesMenosNove;
    }

    public BigDecimal getMesMenosOito() {
        return mesMenosOito;
    }

    public void setMesMenosOito(BigDecimal mesMenosOito) {
        this.mesMenosOito = mesMenosOito;
    }

    public BigDecimal getMesMenosSete() {
        return mesMenosSete;
    }

    public void setMesMenosSete(BigDecimal mesMenosSete) {
        this.mesMenosSete = mesMenosSete;
    }

    public BigDecimal getMesMenosSeis() {
        return mesMenosSeis;
    }

    public void setMesMenosSeis(BigDecimal mesMenosSeis) {
        this.mesMenosSeis = mesMenosSeis;
    }

    public BigDecimal getMesMenosCinco() {
        return mesMenosCinco;
    }

    public void setMesMenosCinco(BigDecimal mesMenosCinco) {
        this.mesMenosCinco = mesMenosCinco;
    }

    public BigDecimal getMesMenosQuatro() {
        return mesMenosQuatro;
    }

    public void setMesMenosQuatro(BigDecimal mesMenosQuatro) {
        this.mesMenosQuatro = mesMenosQuatro;
    }

    public BigDecimal getMesMenosTres() {
        return mesMenosTres;
    }

    public void setMesMenosTres(BigDecimal mesMenosTres) {
        this.mesMenosTres = mesMenosTres;
    }

    public BigDecimal getMesMenosDois() {
        return mesMenosDois;
    }

    public void setMesMenosDois(BigDecimal mesMenosDois) {
        this.mesMenosDois = mesMenosDois;
    }

    public BigDecimal getMesMenosUm() {
        return mesMenosUm;
    }

    public void setMesMenosUm(BigDecimal mesMenosUm) {
        this.mesMenosUm = mesMenosUm;
    }

    public BigDecimal getMesAtual() {
        return mesAtual;
    }

    public void setMesAtual(BigDecimal mesAtual) {
        this.mesAtual = mesAtual;
    }

    public BigDecimal getTotalUltimosMeses() {
        return totalUltimosMeses;
    }

    public void setTotalUltimosMeses(BigDecimal totalUltimosMeses) {
        this.totalUltimosMeses = totalUltimosMeses;
    }

    public BigDecimal getInscritasRestos() {
        return inscritasRestos;
    }

    public void setInscritasRestos(BigDecimal inscritasRestos) {
        this.inscritasRestos = inscritasRestos;
    }
}
