package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class RREOAnexo06Item {

    private String descricao;
    private BigDecimal valorColuna1;
    private BigDecimal valorColuna2;
    private BigDecimal valorColuna3;
    private BigDecimal valorColuna4;
    private BigDecimal valorColuna5;
    private BigDecimal valorColuna6;
    private BigDecimal valorColuna7;
    private Integer ordem;
    private Integer numeroLinhaNoXLS;

    public RREOAnexo06Item() {
        valorColuna1 = BigDecimal.ZERO;
        valorColuna2 = BigDecimal.ZERO;
        valorColuna3 = BigDecimal.ZERO;
        valorColuna4 = BigDecimal.ZERO;
        valorColuna5 = BigDecimal.ZERO;
        valorColuna6 = BigDecimal.ZERO;
        valorColuna7 = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorColuna1() {
        return valorColuna1;
    }

    public void setValorColuna1(BigDecimal valorColuna1) {
        this.valorColuna1 = valorColuna1;
    }

    public BigDecimal getValorColuna2() {
        return valorColuna2;
    }

    public void setValorColuna2(BigDecimal valorColuna2) {
        this.valorColuna2 = valorColuna2;
    }

    public BigDecimal getValorColuna3() {
        return valorColuna3;
    }

    public void setValorColuna3(BigDecimal valorColuna3) {
        this.valorColuna3 = valorColuna3;
    }

    public BigDecimal getValorColuna4() {
        return valorColuna4;
    }

    public void setValorColuna4(BigDecimal valorColuna4) {
        this.valorColuna4 = valorColuna4;
    }

    public BigDecimal getValorColuna5() {
        return valorColuna5;
    }

    public void setValorColuna5(BigDecimal valorColuna5) {
        this.valorColuna5 = valorColuna5;
    }

    public BigDecimal getValorColuna6() {
        return valorColuna6;
    }

    public void setValorColuna6(BigDecimal valorColuna6) {
        this.valorColuna6 = valorColuna6;
    }

    public BigDecimal getValorColuna7() {
        return valorColuna7;
    }

    public void setValorColuna7(BigDecimal valorColuna7) {
        this.valorColuna7 = valorColuna7;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }
}
