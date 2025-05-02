package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RelatorioDocumentoDTO implements Serializable {

    private Long numeroNfse;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private Date emissao;
    private String situacao;
    private String operacao;
    private BigDecimal valorServico;
    private BigDecimal valorIss;

    public RelatorioDocumentoDTO(Long numeroNfse, String cpfCnpj, String nomeRazaoSocial, Date emissao, String situacao,
                                 String operacao, BigDecimal valorServico, BigDecimal valorIss) {
        this.numeroNfse = numeroNfse;
        this.cpfCnpj = cpfCnpj;
        this.nomeRazaoSocial = nomeRazaoSocial;
        this.emissao = emissao;
        this.situacao = situacao;
        this.operacao = operacao;
        this.valorServico = valorServico;
        this.valorIss = valorIss;
    }

    public Long getNumeroNfse() {
        return numeroNfse;
    }

    public void setNumeroNfse(Long numeroNfse) {
        this.numeroNfse = numeroNfse;
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

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }
}
