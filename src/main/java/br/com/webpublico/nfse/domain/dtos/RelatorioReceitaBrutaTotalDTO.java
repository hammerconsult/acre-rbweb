package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class RelatorioReceitaBrutaTotalDTO {

    private Long idCadastro;
    private String inscricaoCadastral;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private Integer ano;
    private String mes;
    private BigDecimal totalServicos;
    private BigDecimal totalIssLancado;
    private BigDecimal totalIssPago;
    private BigDecimal totalJuros;
    private BigDecimal totalMulta;
    private BigDecimal totalCorrecao;
    private String situacao;

    public RelatorioReceitaBrutaTotalDTO() {
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalIssLancado() {
        return totalIssLancado;
    }

    public void setTotalIssLancado(BigDecimal totalIssLancado) {
        this.totalIssLancado = totalIssLancado;
    }

    public BigDecimal getTotalIssPago() {
        return totalIssPago;
    }

    public void setTotalIssPago(BigDecimal totalIssPago) {
        this.totalIssPago = totalIssPago;
    }

    public BigDecimal getTotalJuros() {
        return totalJuros;
    }

    public void setTotalJuros(BigDecimal totalJuros) {
        this.totalJuros = totalJuros;
    }

    public BigDecimal getTotalMulta() {
        return totalMulta;
    }

    public void setTotalMulta(BigDecimal totalMulta) {
        this.totalMulta = totalMulta;
    }

    public BigDecimal getTotalCorrecao() {
        return totalCorrecao;
    }

    public void setTotalCorrecao(BigDecimal totalCorrecao) {
        this.totalCorrecao = totalCorrecao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
