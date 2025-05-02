package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.CaracteristicaConstrucaoHabitese;

import java.math.BigDecimal;

public class WsCaracteristicaConstrucaoHabitese {

    private String tipoConstrucao;
    private String tipoHabitese;
    private Integer quantidadeDePavimentos;
    private Integer tempoConstrucao;
    private BigDecimal valorMaoDeObra;
    private BigDecimal areaConstruida;
    private BigDecimal baseDeCalculo;
    private String classe;
    private String padrao;

    public WsCaracteristicaConstrucaoHabitese() {
    }

    public WsCaracteristicaConstrucaoHabitese(CaracteristicaConstrucaoHabitese caracteristica) {
        this.tipoConstrucao = caracteristica.getTipoConstrucao().getDescricao();
        this.tipoHabitese = caracteristica.getTipoHabitese().getDescricao();
        this.quantidadeDePavimentos = caracteristica.getQuantidadeDePavimentos();
        this.tempoConstrucao = caracteristica.getTempoConstrucao();
        this.areaConstruida = caracteristica.getAreaConstruida();
        this.baseDeCalculo = caracteristica.getBaseDeCalculo();
        this.classe = caracteristica.getClasse().getDescricao();
        this.padrao = caracteristica.getPadrao().getDescricao();
    }

    public String getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(String tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    public String getTipoHabitese() {
        return tipoHabitese;
    }

    public void setTipoHabitese(String tipoHabitese) {
        this.tipoHabitese = tipoHabitese;
    }

    public Integer getQuantidadeDePavimentos() {
        return quantidadeDePavimentos;
    }

    public void setQuantidadeDePavimentos(Integer quantidadeDePavimentos) {
        this.quantidadeDePavimentos = quantidadeDePavimentos;
    }

    public Integer getTempoConstrucao() {
        return tempoConstrucao;
    }

    public void setTempoConstrucao(Integer tempoConstrucao) {
        this.tempoConstrucao = tempoConstrucao;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getBaseDeCalculo() {
        return baseDeCalculo;
    }

    public void setBaseDeCalculo(BigDecimal baseDeCalculo) {
        this.baseDeCalculo = baseDeCalculo;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getPadrao() {
        return padrao;
    }

    public void setPadrao(String padrao) {
        this.padrao = padrao;
    }
}
