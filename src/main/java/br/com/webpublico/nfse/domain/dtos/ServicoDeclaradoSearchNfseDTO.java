package br.com.webpublico.nfse.domain.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicoDeclaradoSearchNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private Long numero;
    private Date emissao;
    private String tipoServicoDeclarado;
    private Long tipoDocumentoCodigo;
    private String tipoDocumentoDescricao;
    private String tomadorCpfCnpj;
    private String tomadorNomeRazaoSocial;
    private String prestadorCpfCnpj;
    private String prestadorNomeRazaoSocial;
    private Boolean issRetido;
    private BigDecimal totalServicos;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculado;
    private String situacao;
    private String naturezaOperacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getTipoServicoDeclarado() {
        return tipoServicoDeclarado;
    }

    public void setTipoServicoDeclarado(String tipoServicoDeclarado) {
        this.tipoServicoDeclarado = tipoServicoDeclarado;
    }

    public Long getTipoDocumentoCodigo() {
        return tipoDocumentoCodigo;
    }

    public void setTipoDocumentoCodigo(Long tipoDocumentoCodigo) {
        this.tipoDocumentoCodigo = tipoDocumentoCodigo;
    }

    public String getTipoDocumentoDescricao() {
        return tipoDocumentoDescricao;
    }

    public void setTipoDocumentoDescricao(String tipoDocumentoDescricao) {
        this.tipoDocumentoDescricao = tipoDocumentoDescricao;
    }

    public String getTomadorCpfCnpj() {
        return tomadorCpfCnpj;
    }

    public void setTomadorCpfCnpj(String tomadorCpfCnpj) {
        this.tomadorCpfCnpj = tomadorCpfCnpj;
    }

    public String getTomadorNomeRazaoSocial() {
        return tomadorNomeRazaoSocial;
    }

    public void setTomadorNomeRazaoSocial(String tomadorNomeRazaoSocial) {
        this.tomadorNomeRazaoSocial = tomadorNomeRazaoSocial;
    }

    public String getPrestadorCpfCnpj() {
        return prestadorCpfCnpj;
    }

    public void setPrestadorCpfCnpj(String prestadorCpfCnpj) {
        this.prestadorCpfCnpj = prestadorCpfCnpj;
    }

    public String getPrestadorNomeRazaoSocial() {
        return prestadorNomeRazaoSocial;
    }

    public void setPrestadorNomeRazaoSocial(String prestadorNomeRazaoSocial) {
        this.prestadorNomeRazaoSocial = prestadorNomeRazaoSocial;
    }

    public Boolean getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }
}
