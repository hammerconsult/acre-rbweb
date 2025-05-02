package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 14/03/2017.
 */
public class DamAgrupado {
    private String numeroDam;
    private Date vencimento;
    private BigDecimal valorOriginal;
    private BigDecimal correcaoMonetaria;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private BigDecimal valor;
    private String codigoBarrasDigitos;
    private String codigoBarras;
    private String instrucaoLinha1;
    private String instrucaoLinha2;
    private String instrucaoLinha3;
    private String instrucaoLinha4;
    private String instrucaoLinha5;
    private String contribuinte;
    private String cpfCnpj;
    private String endereco;
    private String qrCodePix;
    private BigDecimal numeroEmissao;
    private List<ResultadoParcela> resultadoParcelas;
    private List<DamAgrupadoLancamentos> lancamentos;

    public DamAgrupado() {
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

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public String getInstrucaoLinha1() {
        return instrucaoLinha1;
    }

    public void setInstrucaoLinha1(String instrucaoLinha1) {
        this.instrucaoLinha1 = instrucaoLinha1;
    }

    public String getInstrucaoLinha2() {
        return instrucaoLinha2;
    }

    public void setInstrucaoLinha2(String instrucaoLinha2) {
        this.instrucaoLinha2 = instrucaoLinha2;
    }

    public String getInstrucaoLinha3() {
        return instrucaoLinha3;
    }

    public void setInstrucaoLinha3(String instrucaoLinha3) {
        this.instrucaoLinha3 = instrucaoLinha3;
    }

    public String getInstrucaoLinha4() {
        return instrucaoLinha4;
    }

    public void setInstrucaoLinha4(String instrucaoLinha4) {
        this.instrucaoLinha4 = instrucaoLinha4;
    }

    public String getInstrucaoLinha5() {
        return instrucaoLinha5;
    }

    public void setInstrucaoLinha5(String instrucaoLinha5) {
        this.instrucaoLinha5 = instrucaoLinha5;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getNumeroEmissao() {
        return numeroEmissao;
    }

    public void setNumeroEmissao(BigDecimal numeroEmissao) {
        this.numeroEmissao = numeroEmissao;
    }

    public List<ResultadoParcela> getResultadoParcelas() {
        return resultadoParcelas;
    }

    public void setResultadoParcelas(List<ResultadoParcela> resultadoParcelas) {
        this.resultadoParcelas = resultadoParcelas;
    }

    public List<DamAgrupadoLancamentos> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<DamAgrupadoLancamentos> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public String getQrCodePix() {
        return qrCodePix;
    }

    public void setQrCodePix(String qrCodePix) {
        this.qrCodePix = qrCodePix;
    }
}
