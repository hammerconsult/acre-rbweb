package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

/**
 * Created by rodolfo on 23/07/18.
 */
public class RelatorioLivroFiscalServicoDTO implements Comparable<RelatorioLivroFiscalServicoDTO> {
    private String servico;
    private Integer quantidade;
    private BigDecimal valorServico;
    private BigDecimal deducoes;
    private BigDecimal descontosIncondicionaos;
    private BigDecimal descontosCondicionados;
    private BigDecimal reducaoBaseCalculo;
    private BigDecimal baseCalculo;
    private BigDecimal aliquota;
    private BigDecimal iss;
    private Boolean retido;

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getDescontosIncondicionaos() {
        return descontosIncondicionaos;
    }

    public void setDescontosIncondicionaos(BigDecimal descontosIncondicionaos) {
        this.descontosIncondicionaos = descontosIncondicionaos;
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public BigDecimal getReducaoBaseCalculo() {
        return reducaoBaseCalculo;
    }

    public void setReducaoBaseCalculo(BigDecimal reducaoBaseCalculo) {
        this.reducaoBaseCalculo = reducaoBaseCalculo;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Boolean getRetido() {
        return retido;
    }

    public void setRetido(Boolean retido) {
        this.retido = retido;
    }

    @Override
    public int compareTo(RelatorioLivroFiscalServicoDTO o) {
        return this.servico.compareTo(o.toString());
    }
}
