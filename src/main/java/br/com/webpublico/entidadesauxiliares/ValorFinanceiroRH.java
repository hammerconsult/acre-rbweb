package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeOrganizacional;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by peixe on 19/08/2015.
 */
public class ValorFinanceiroRH implements Serializable {
    private UnidadeOrganizacional unidadeOrganizacional;
    private BigDecimal totalVantagem;
    private BigDecimal totalDesconto;
    private BigDecimal totalBase;

    public ValorFinanceiroRH() {
        totalVantagem = BigDecimal.ZERO;
        totalDesconto = BigDecimal.ZERO;
        totalBase = BigDecimal.ZERO;
    }

    public ValorFinanceiroRH(BigDecimal totalVantagem, BigDecimal totalDesconto) {
        this.totalVantagem = totalVantagem;
        this.totalDesconto = totalDesconto;
    }

    public ValorFinanceiroRH(UnidadeOrganizacional unidadeOrganizacional, BigDecimal totalVantagem, BigDecimal totalDesconto) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.totalVantagem = totalVantagem;
        this.totalDesconto = totalDesconto;
    }

    public ValorFinanceiroRH(BigDecimal totalVantagem, BigDecimal totalDesconto, BigDecimal totalBase) {
        this.totalVantagem = totalVantagem;
        this.totalDesconto = totalDesconto;
        this.totalBase = totalBase;
    }

    public BigDecimal getTotalBase() {
        return totalBase;
    }

    public void setTotalBase(BigDecimal totalBase) {
        this.totalBase = totalBase;
    }

    public BigDecimal getTotalVantagem() {
        return totalVantagem;
    }

    public void setTotalVantagem(BigDecimal totalVantagem) {
        this.totalVantagem = totalVantagem;
    }

    public BigDecimal getTotalDesconto() {
        return totalDesconto;
    }

    public void setTotalDesconto(BigDecimal totalDesconto) {
        this.totalDesconto = totalDesconto;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    @Override
    public String toString() {
        return "ValorFinanceiroRH{" +
            "totalVantagem=" + totalVantagem +
            ", totalDesconto=" + totalDesconto +
            ", totalBase=" + totalBase +
            '}';
    }
}
