package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

/**
 * Created by rodolfo on 23/07/18.
 */
public class RelatorioLivroFiscalResumoDTO implements Comparable<RelatorioLivroFiscalResumoDTO> {
    private String servico;
    private BigDecimal aliquota;
    private BigDecimal valorServico;
    private BigDecimal deducoes;
    private BigDecimal descontosIncondicionaos;
    private BigDecimal descontosCondicionados;
    private BigDecimal baseCalculo;
    private BigDecimal iss;
    private Boolean retido;
    private Integer quantidadeNotas;

    public RelatorioLivroFiscalResumoDTO() {
        this.aliquota = BigDecimal.ZERO;
        this.valorServico = BigDecimal.ZERO;
        this.deducoes = BigDecimal.ZERO;
        this.descontosIncondicionaos = BigDecimal.ZERO;
        this.descontosCondicionados = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.iss = BigDecimal.ZERO;
        this.quantidadeNotas = 0;
    }

    public RelatorioLivroFiscalResumoDTO(String servico, BigDecimal aliquota, Boolean issRetido) {
        this();
        this.servico = servico;
        this.aliquota = aliquota;
        this.retido = issRetido;
    }

    public void calcularServico(RelatorioLivroFiscalServicoDTO servico) {
        addValorServico(servico.getValorServico());
        addDeducoes(servico.getDeducoes());
        addDescontosIncondicionaos(servico.getDescontosIncondicionaos());
        addDescontosCondicionados(servico.getDescontosCondicionados());
        addBaseCalculo(servico.getBaseCalculo());
        addIss(servico.getIss());
        quantidadeNotas++;
    }

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

    public void addValorServico(BigDecimal valorServico) {
        if (this.valorServico == null) {
            this.valorServico = BigDecimal.ZERO;
        }
        this.valorServico = this.valorServico.add(valorServico);
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public void addDeducoes(BigDecimal deducoes) {
        if (this.deducoes == null) {
            this.deducoes = BigDecimal.ZERO;
        }
        this.deducoes = this.deducoes.add(deducoes);
    }

    public BigDecimal getDescontosIncondicionaos() {
        return descontosIncondicionaos;
    }

    public void setDescontosIncondicionaos(BigDecimal descontosIncondicionaos) {
        this.descontosIncondicionaos = descontosIncondicionaos;
    }

    public void addDescontosIncondicionaos(BigDecimal descontosIncondicionaos) {
        if (this.descontosIncondicionaos == null) {
            this.descontosIncondicionaos = BigDecimal.ZERO;
        }
        this.descontosIncondicionaos = this.descontosIncondicionaos.add(descontosIncondicionaos != null ? descontosIncondicionaos : BigDecimal.ZERO);
    }

    public BigDecimal getDescontosCondicionados() {
        return descontosCondicionados;
    }

    public void setDescontosCondicionados(BigDecimal descontosCondicionados) {
        this.descontosCondicionados = descontosCondicionados;
    }

    public void addDescontosCondicionados(BigDecimal descontosCondicionados) {
        if (this.descontosCondicionados == null) {
            this.descontosCondicionados = BigDecimal.ZERO;
        }
        
        this.descontosCondicionados = this.descontosCondicionados.add(descontosCondicionados != null ? descontosCondicionados : BigDecimal.ZERO);
    }

    public Integer getQuantidadeNotas() {
        return quantidadeNotas;
    }

    public void setQuantidadeNotas(Integer quantidadeNotas) {
        this.quantidadeNotas = quantidadeNotas;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public void addBaseCalculo(BigDecimal baseCalculo) {
        if (this.baseCalculo == null) {
            this.baseCalculo = BigDecimal.ZERO;
        }
        this.baseCalculo = this.baseCalculo.add(baseCalculo);
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

    public void addIss(BigDecimal iss) {
        if (this.iss == null) {
            this.iss = BigDecimal.ZERO;
        }
        this.iss = this.aliquota.add(iss);
    }

    public Boolean getRetido() {
        return retido != null ? retido : false;
    }

    public void setRetido(Boolean retido) {
        this.retido = retido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatorioLivroFiscalResumoDTO that = (RelatorioLivroFiscalResumoDTO) o;

        if (servico != null ? !servico.equals(that.servico) : that.servico != null) return false;
        if (aliquota != null ? !aliquota.equals(that.aliquota) : that.aliquota != null) return false;
        return retido != null ? retido.equals(that.retido) : that.retido == null;
    }

    @Override
    public int hashCode() {
        int result = servico != null ? servico.hashCode() : 0;
        result = 31 * result + (aliquota != null ? aliquota.hashCode() : 0);
        result = 31 * result + (retido != null ? retido.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(RelatorioLivroFiscalResumoDTO o) {
        return this.getRetido().compareTo(o.getRetido());
    }
}
