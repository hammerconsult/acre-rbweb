package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CaracteristicaConstrucaoHabitese;
import br.com.webpublico.entidades.ServicosAlvaraConstrucao;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class VOCaracteristicaConstrucaoHabitese {

    private String tipoConstrucao;
    private String tipoHabitese;
    private Integer quantidadeDePavimentos;
    private Integer tempoConstrucao;
    private BigDecimal valorMaoDeObra;
    private BigDecimal areaConstruida;
    private BigDecimal baseDeCalculo;
    private List<VOServicosAlvaraConstrucao> servicos;
    private String classe;
    private String padrao;
    private String faixaDeValor;

    public VOCaracteristicaConstrucaoHabitese(CaracteristicaConstrucaoHabitese caracteristica) {
        this.tipoConstrucao = caracteristica.getTipoConstrucao().getDescricao();
        this.tipoHabitese = caracteristica.getTipoHabitese().getDescricao();
        this.quantidadeDePavimentos = caracteristica.getQuantidadeDePavimentos();
        this.tempoConstrucao = caracteristica.getTempoConstrucao();
        this.valorMaoDeObra = caracteristica.getValorMaoDeObra();
        this.areaConstruida = caracteristica.getAreaConstruida();
        this.baseDeCalculo = caracteristica.getBaseDeCalculo();
        this.servicos = Lists.newArrayList();
        for (ServicosAlvaraConstrucao servico : caracteristica.getServicos()) {
            this.servicos.add(new VOServicosAlvaraConstrucao(servico));
        }
        this.classe = caracteristica.getClasse().toString();
        this.padrao = caracteristica.getPadrao().toString();
        this.faixaDeValor = caracteristica.getFaixaDeValor().toString();
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

    public List<VOServicosAlvaraConstrucao> getServicos() {
        return servicos;
    }

    public void setServicos(List<VOServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
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

    public String getFaixaDeValor() {
        return faixaDeValor;
    }

    public void setFaixaDeValor(String faixaDeValor) {
        this.faixaDeValor = faixaDeValor;
    }
}
