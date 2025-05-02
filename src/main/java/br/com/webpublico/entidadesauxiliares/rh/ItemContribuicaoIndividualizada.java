package br.com.webpublico.entidadesauxiliares.rh;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author peixe on 03/12/2015  11:38.
 */
public class ItemContribuicaoIndividualizada implements Serializable {
    private String mes;
    private Integer ano;
    private BigDecimal salarioBruto;
    private BigDecimal basePrevidencia;
    private BigDecimal contribuicaoServidor;
    private BigDecimal contribuicaoPatronal;
    private BigDecimal aliquotaSuplementar;
    private BigDecimal totalDeContribuicao;

    public ItemContribuicaoIndividualizada() {
        salarioBruto = BigDecimal.ZERO;
        basePrevidencia = BigDecimal.ZERO;
        contribuicaoServidor = BigDecimal.ZERO;
        contribuicaoPatronal = BigDecimal.ZERO;
        aliquotaSuplementar = BigDecimal.ZERO;
        totalDeContribuicao = BigDecimal.ZERO;

    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(BigDecimal salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public BigDecimal getBasePrevidencia() {
        return basePrevidencia;
    }

    public void setBasePrevidencia(BigDecimal basePrevidencia) {
        this.basePrevidencia = basePrevidencia;
    }

    public BigDecimal getContribuicaoServidor() {
        return contribuicaoServidor;
    }

    public void setContribuicaoServidor(BigDecimal contribuicaoServidor) {
        this.contribuicaoServidor = contribuicaoServidor;
    }

    public BigDecimal getContribuicaoPatronal() {
        return contribuicaoPatronal;
    }

    public void setContribuicaoPatronal(BigDecimal contribuicaoPatronal) {
        this.contribuicaoPatronal = contribuicaoPatronal;
    }

    public BigDecimal getAliquotaSuplementar() {
        return aliquotaSuplementar;
    }

    public void setAliquotaSuplementar(BigDecimal aliquotaSuplementar) {
        this.aliquotaSuplementar = aliquotaSuplementar;
    }

    public BigDecimal getTotalDeContribuicao() {
        return totalDeContribuicao;
    }

    public void setTotalDeContribuicao(BigDecimal totalDeContribuicao) {
        this.totalDeContribuicao = totalDeContribuicao;
    }
}
