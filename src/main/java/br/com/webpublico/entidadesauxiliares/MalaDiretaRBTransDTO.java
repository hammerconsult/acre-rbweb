package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Wellington Abdo on 23/12/2016.
 */
public class MalaDiretaRBTransDTO implements Serializable {

    private String texto;
    private String contribuinte;
    private String cpfCnpj;
    private String inscricao;
    private String tipoAutonomo;
    private String tipoPermissao;
    private Integer numeroPermissao;
    private String tributo;
    private String processos;
    private String codigoBarrasDigito;
    private String codigoBarras;
    private Date emissao;
    private String numeroDam;
    private Date vencimento;
    private BigDecimal valorOriginal;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal honorarios;
    private BigDecimal correcaoMonetaria;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
    private String endereco;
    private Long id;
    private String qrCodePix;
    private Long idDam;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(String tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public Integer getNumeroPermissao() {
        return numeroPermissao;
    }

    public void setNumeroPermissao(Integer numeroPermissao) {
        this.numeroPermissao = numeroPermissao;
    }

    public String getTributo() {
        return tributo;
    }

    public void setTributo(String tributo) {
        this.tributo = tributo;
    }

    public String getProcessos() {
        return processos;
    }

    public void setProcessos(String processos) {
        this.processos = processos;
    }

    public String getCodigoBarrasDigito() {
        return codigoBarrasDigito;
    }

    public void setCodigoBarrasDigito(String codigoBarrasDigito) {
        this.codigoBarrasDigito = codigoBarrasDigito;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getNumeroDam() {
        return numeroDam;
    }

    public void setNumeroDam(String numeroDam) {
        this.numeroDam = numeroDam;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoPermissao() {
        return tipoPermissao;
    }

    public void setTipoPermissao(String tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }
}
