package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CabecalhoArquivoCosifDTO {
    private String identificacao;
    private String cnpj;
    private String razaoSocial;
    private String mes;
    private Integer ano;
    private Date dataMovimento;
    private Integer totalRegistro;
    private BigDecimal valorTotalServico;
    private BigDecimal valorTotalISSQN;
    private List<DetalheArquivoCosifDTO> linhas;

    public CabecalhoArquivoCosifDTO() {
        linhas = Lists.newArrayList();
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Integer getTotalRegistro() {
        return totalRegistro;
    }

    public void setTotalRegistro(Integer totalRegistro) {
        this.totalRegistro = totalRegistro;
    }

    public BigDecimal getValorTotalServico() {
        return valorTotalServico;
    }

    public void setValorTotalServico(BigDecimal valorTotalServico) {
        this.valorTotalServico = valorTotalServico;
    }

    public BigDecimal getValorTotalISSQN() {
        return valorTotalISSQN;
    }

    public void setValorTotalISSQN(BigDecimal valorTotalISSQN) {
        this.valorTotalISSQN = valorTotalISSQN;
    }

    public List<DetalheArquivoCosifDTO> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<DetalheArquivoCosifDTO> linhas) {
        this.linhas = linhas;
    }
}
