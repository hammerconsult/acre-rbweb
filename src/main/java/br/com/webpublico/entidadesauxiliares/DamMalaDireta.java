package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fabio on 07/06/2016.
 */
public class DamMalaDireta {

    private Long idDam;
    private Date vencimento;
    private String numeroDam;
    private BigDecimal valorOriginal;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;
    private BigDecimal valorCorrecao;
    private BigDecimal valorJuros;
    private BigDecimal valorMulta;
    private BigDecimal valorHonorarios;
    private BigDecimal valorDesconto;
    private BigDecimal valorDam;
    private String codigoBarrasDigitos;
    private String codigoBarras;
    private String inscricaoCadastral;
    private String cpfCnpj;
    private String contributinte;
    private String logradouro;
    private String logradouroComplemento;
    private String bairro;
    private String cep;
    private Integer exercicio;
    private String parcelas;
    private String texto;
    private String qrCodePix;

    public DamMalaDireta() {
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getNumeroDam() {
        return numeroDam;
    }

    public void setNumeroDam(String numeroDam) {
        this.numeroDam = numeroDam;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorDam() {
        return valorDam;
    }

    public void setValorDam(BigDecimal valorDam) {
        this.valorDam = valorDam;
    }

    public String getCodigoBarrasDigitos() {
        return codigoBarrasDigitos;
    }

    public void setCodigoBarrasDigitos(String codigoBarrasDigitos) {
        this.codigoBarrasDigitos = codigoBarrasDigitos;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public String getContributinte() {
        return contributinte;
    }

    public void setContributinte(String contributinte) {
        this.contributinte = contributinte;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getLogradouroComplemento() {
        return logradouroComplemento;
    }

    public void setLogradouroComplemento(String logradouroComplemento) {
        this.logradouroComplemento = logradouroComplemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getParcelas() {
        return parcelas;
    }

    public void setParcelas(String parcelas) {
        this.parcelas = parcelas;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }
}
