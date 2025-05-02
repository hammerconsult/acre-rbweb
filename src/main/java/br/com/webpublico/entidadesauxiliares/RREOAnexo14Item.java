package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 12/09/2014.
 */
public class RREOAnexo14Item {

    private String descricao;
    private BigDecimal valorColuna1;
    private BigDecimal valorColuna2;
    private BigDecimal valorColuna3;
    private BigDecimal valorColuna4;
    private Integer numeroLinhaNoXLS;

    public RREOAnexo14Item() {
        valorColuna1 = BigDecimal.ZERO;
        valorColuna2 = BigDecimal.ZERO;
        valorColuna3 = BigDecimal.ZERO;
        valorColuna4 = BigDecimal.ZERO;
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

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }
}
