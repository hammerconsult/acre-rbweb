package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class EstatisticaMensalPorTomadorNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Integer mes;
    private Integer ano;
    private String nome;
    private String cnpj;
    private BigDecimal totalServico;
    private BigDecimal totalPagar;

    public EstatisticaMensalPorTomadorNfseDTO(Integer mes, Integer ano) {
        this();
        this.mes = mes;
        this.ano = ano;

    }

    public EstatisticaMensalPorTomadorNfseDTO() {
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public BigDecimal getTotalServico() {
        return totalServico;
    }

    public void setTotalServico(BigDecimal totalServico) {
        this.totalServico = totalServico;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
