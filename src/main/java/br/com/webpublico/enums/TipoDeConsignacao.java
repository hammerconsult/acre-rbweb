package br.com.webpublico.enums;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 03/09/14
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */
public enum TipoDeConsignacao {

    CONVENIO("Convênio", 10),
    EMPRESTIMO("Empréstimo", 20),
    CARTAO("Cartão", 5),
    EUCONSIGOMAIS("Salary Pay", 20),
    CONSIGNADO("Consignado", 30);


    private String descricao;
    private Integer percentual;

    private TipoDeConsignacao(String descricao, Integer percentual) {
        this.descricao = descricao;
        this.percentual = percentual;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getPercentual() {
        return percentual;
    }

    public BigDecimal getPercentualDecimal() {
        return new BigDecimal(percentual);
    }

    public static BigDecimal getPercentualConsigancao() {
        Integer percentual = 0;
        percentual += CARTAO.getPercentualDecimal().intValue();
        percentual += EMPRESTIMO.getPercentualDecimal().intValue();
        percentual += CONVENIO.getPercentualDecimal().intValue();
        return new BigDecimal(percentual);
    }

    @Override
    public String toString() {
        return descricao + " - " + percentual + '%';
    }
}
