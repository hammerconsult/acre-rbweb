package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/10/13
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class RREOAnexo08Item13 {
    private String descricao;
    private BigDecimal fundeb;
    private BigDecimal salarioEducacao;
    private Integer nivel;

    public RREOAnexo08Item13() {
        salarioEducacao = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getFundeb() {
        return fundeb;
    }

    public void setFundeb(BigDecimal fundeb) {
        this.fundeb = fundeb;
    }

    public BigDecimal getSalarioEducacao() {
        return salarioEducacao;
    }

    public void setSalarioEducacao(BigDecimal salarioEducacao) {
        this.salarioEducacao = salarioEducacao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
