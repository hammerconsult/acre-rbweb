package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class AvaliacaoEconomicaAbrasf implements Serializable {
    private String periodoSemana;
    private String descricaoSessao1;
    private String descricaoSessao2;
    private String descricaoSessao3;
    private String descricaoSessao4;
    private Integer ano;
    private Integer anoAnterior;
    private BigDecimal sessao1;
    private BigDecimal sessao1Anterior;
    private BigDecimal sessao2;
    private BigDecimal sessao2Anterior;
    private BigDecimal sessao3;
    private BigDecimal sessao3Anterior;
    private BigDecimal sessao4;
    private BigDecimal sessao4Anterior;

    public AvaliacaoEconomicaAbrasf(Integer ano) {
        if (ano != null) {
            this.ano = ano;
            this.anoAnterior = (ano - 1);
        }
        this.sessao1 = BigDecimal.ZERO;
        this.sessao1Anterior = BigDecimal.ZERO;
        this.sessao2 = BigDecimal.ZERO;
        this.sessao2Anterior = BigDecimal.ZERO;
        this.sessao3 = BigDecimal.ZERO;
        this.sessao3Anterior = BigDecimal.ZERO;
        this.sessao4 = BigDecimal.ZERO;
        this.sessao4Anterior = BigDecimal.ZERO;
    }

    public String getPeriodoSemana() {
        return periodoSemana;
    }

    public void setPeriodoSemana(String periodoSemana) {
        this.periodoSemana = periodoSemana;
    }

    public String getDescricaoSessao1() {
        return descricaoSessao1;
    }

    public void setDescricaoSessao1(String descricaoSessao1) {
        this.descricaoSessao1 = descricaoSessao1;
    }

    public String getDescricaoSessao2() {
        return descricaoSessao2;
    }

    public void setDescricaoSessao2(String descricaoSessao2) {
        this.descricaoSessao2 = descricaoSessao2;
    }

    public String getDescricaoSessao3() {
        return descricaoSessao3;
    }

    public void setDescricaoSessao3(String descricaoSessao3) {
        this.descricaoSessao3 = descricaoSessao3;
    }

    public String getDescricaoSessao4() {
        return descricaoSessao4;
    }

    public void setDescricaoSessao4(String descricaoSessao4) {
        this.descricaoSessao4 = descricaoSessao4;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getAnoAnterior() {
        return anoAnterior;
    }

    public void setAnoAnterior(Integer anoAnterior) {
        this.anoAnterior = anoAnterior;
    }

    public BigDecimal getSessao1() {
        return sessao1;
    }

    public void setSessao1(BigDecimal sessao1) {
        this.sessao1 = sessao1;
    }

    public BigDecimal getSessao1Anterior() {
        return sessao1Anterior;
    }

    public void setSessao1Anterior(BigDecimal sessao1Anterior) {
        this.sessao1Anterior = sessao1Anterior;
    }

    public BigDecimal getSessao2() {
        return sessao2;
    }

    public void setSessao2(BigDecimal sessao2) {
        this.sessao2 = sessao2;
    }

    public BigDecimal getSessao2Anterior() {
        return sessao2Anterior;
    }

    public void setSessao2Anterior(BigDecimal sessao2Anterior) {
        this.sessao2Anterior = sessao2Anterior;
    }

    public BigDecimal getSessao3() {
        return sessao3;
    }

    public void setSessao3(BigDecimal sessao3) {
        this.sessao3 = sessao3;
    }

    public BigDecimal getSessao3Anterior() {
        return sessao3Anterior;
    }

    public void setSessao3Anterior(BigDecimal sessao3Anterior) {
        this.sessao3Anterior = sessao3Anterior;
    }

    public BigDecimal getSessao4() {
        return sessao4;
    }

    public void setSessao4(BigDecimal sessao4) {
        this.sessao4 = sessao4;
    }

    public BigDecimal getSessao4Anterior() {
        return sessao4Anterior;
    }

    public void setSessao4Anterior(BigDecimal sessao4Anterior) {
        this.sessao4Anterior = sessao4Anterior;
    }
}
