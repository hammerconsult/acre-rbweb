package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ExecucaoOperacaoCreditoVo {
    private String divida;
    private String programa;
    private String unidade;
    private BigDecimal normais;
    private BigDecimal restos;
    private BigDecimal total;

    public ExecucaoOperacaoCreditoVo() {
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getNormais() {
        return normais;
    }

    public void setNormais(BigDecimal normais) {
        this.normais = normais;
    }

    public BigDecimal getRestos() {
        return restos;
    }

    public void setRestos(BigDecimal restos) {
        this.restos = restos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
