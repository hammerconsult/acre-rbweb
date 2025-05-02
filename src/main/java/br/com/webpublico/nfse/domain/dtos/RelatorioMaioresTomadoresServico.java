package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioMaioresTomadoresServico {

    private Long idDeclaracao;
    private Long idPessoa;
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

    public RelatorioMaioresTomadoresServico() {
        totalServicos = BigDecimal.ZERO;
        issPagar = BigDecimal.ZERO;
        issCalculado = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
    }

    public Long getIdDeclaracao() {
        return idDeclaracao;
    }

    public void setIdDeclaracao(Long idDeclaracao) {
        this.idDeclaracao = idDeclaracao;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
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
}
