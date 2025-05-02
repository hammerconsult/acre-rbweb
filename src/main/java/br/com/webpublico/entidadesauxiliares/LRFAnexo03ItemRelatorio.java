/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author juggernaut
 */
public class LRFAnexo03ItemRelatorio {
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
    private BigDecimal previsaoAtualizada;
    private Integer nivel;
    private Integer numeroLinhaNoXLS;
    private Integer ordem;

    public LRFAnexo03ItemRelatorio() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getMesAtual() {
        return mesAtual;
    }

    public void setMesAtual(BigDecimal mesAtual) {
        this.mesAtual = mesAtual;
    }

    public BigDecimal getMesMenosCinco() {
        return mesMenosCinco;
    }

    public void setMesMenosCinco(BigDecimal mesMenosCinco) {
        this.mesMenosCinco = mesMenosCinco;
    }

    public BigDecimal getMesMenosDez() {
        return mesMenosDez;
    }

    public void setMesMenosDez(BigDecimal mesMenosDez) {
        this.mesMenosDez = mesMenosDez;
    }

    public BigDecimal getMesMenosDois() {
        return mesMenosDois;
    }

    public void setMesMenosDois(BigDecimal mesMenosDois) {
        this.mesMenosDois = mesMenosDois;
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

    public BigDecimal getMesMenosOnze() {
        return mesMenosOnze;
    }

    public void setMesMenosOnze(BigDecimal mesMenosOnze) {
        this.mesMenosOnze = mesMenosOnze;
    }

    public BigDecimal getMesMenosQuatro() {
        return mesMenosQuatro;
    }

    public void setMesMenosQuatro(BigDecimal mesMenosQuatro) {
        this.mesMenosQuatro = mesMenosQuatro;
    }

    public BigDecimal getMesMenosSeis() {
        return mesMenosSeis;
    }

    public void setMesMenosSeis(BigDecimal mesMenosSeis) {
        this.mesMenosSeis = mesMenosSeis;
    }

    public BigDecimal getMesMenosSete() {
        return mesMenosSete;
    }

    public void setMesMenosSete(BigDecimal mesMenosSete) {
        this.mesMenosSete = mesMenosSete;
    }

    public BigDecimal getMesMenosTres() {
        return mesMenosTres;
    }

    public void setMesMenosTres(BigDecimal mesMenosTres) {
        this.mesMenosTres = mesMenosTres;
    }

    public BigDecimal getMesMenosUm() {
        return mesMenosUm;
    }

    public void setMesMenosUm(BigDecimal mesMenosUm) {
        this.mesMenosUm = mesMenosUm;
    }

    public BigDecimal getPrevisaoAtualizada() {
        return previsaoAtualizada;
    }

    public void setPrevisaoAtualizada(BigDecimal previsaoAtualizada) {
        this.previsaoAtualizada = previsaoAtualizada;
    }

    public BigDecimal getTotalUltimosMeses() {
        return totalUltimosMeses;
    }

    public void setTotalUltimosMeses(BigDecimal totalUltimosMeses) {
        this.totalUltimosMeses = totalUltimosMeses;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
