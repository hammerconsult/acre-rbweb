package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioDemonstrativoNotasPorSituacao {

    private String cadastroEconomico;
    private String servico;
    private Long numero;
    private Date dataEmissao;
    private String nomeRazacaoSocial;
    private String cpfCnpj;
    private String situcao;
    private String tipoDocumento;
    private BigDecimal totalServicos;
    private BigDecimal issPagar;
    private BigDecimal issCalculado;
    private BigDecimal baseCalculo;

    private BigDecimal valorServico;
    private BigDecimal issServico;
    private BigDecimal baseCalculoServico;
    private BigDecimal aliquota;
    private BigDecimal deducoes;

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNomeRazacaoSocial() {
        return nomeRazacaoSocial;
    }

    public void setNomeRazacaoSocial(String nomeRazacaoSocial) {
        this.nomeRazacaoSocial = nomeRazacaoSocial;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getSitucao() {
        return situcao;
    }

    public void setSitucao(String situcao) {
        this.situcao = situcao;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getIssPagar() {
        return issPagar;
    }

    public void setIssPagar(BigDecimal issPagar) {
        this.issPagar = issPagar;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
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

    public BigDecimal getIssServico() {
        return issServico;
    }

    public void setIssServico(BigDecimal issServico) {
        this.issServico = issServico;
    }

    public BigDecimal getBaseCalculoServico() {
        return baseCalculoServico;
    }

    public void setBaseCalculoServico(BigDecimal baseCalculoServico) {
        this.baseCalculoServico = baseCalculoServico;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }
}
