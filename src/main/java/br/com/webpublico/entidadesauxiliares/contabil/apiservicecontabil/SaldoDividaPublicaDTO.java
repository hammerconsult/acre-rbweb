package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoDividaPublicaDTO {
    private Long id;
    private LocalDate dataSaldo;
    private BigDecimal valor;
    private Long idUnidadeOrganizacional;
    private Long idDividaPublica;
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    private Boolean isEstorno;
    private OperacaoDiarioDividaPublica operacaoDiarioDividaPublica;
    private Intervalo intervalo;
    private Long idContaDeDestinacao;
    private Boolean validarValorNegativo;

    public SaldoDividaPublicaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(LocalDate dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public Long getIdDividaPublica() {
        return idDividaPublica;
    }

    public void setIdDividaPublica(Long idDividaPublica) {
        this.idDividaPublica = idDividaPublica;
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public Boolean getEstorno() {
        return isEstorno;
    }

    public void setEstorno(Boolean estorno) {
        isEstorno = estorno;
    }

    public OperacaoDiarioDividaPublica getOperacaoDiarioDividaPublica() {
        return operacaoDiarioDividaPublica;
    }

    public void setOperacaoDiarioDividaPublica(OperacaoDiarioDividaPublica operacaoDiarioDividaPublica) {
        this.operacaoDiarioDividaPublica = operacaoDiarioDividaPublica;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public Long getIdContaDeDestinacao() {
        return idContaDeDestinacao;
    }

    public void setIdContaDeDestinacao(Long idContaDeDestinacao) {
        this.idContaDeDestinacao = idContaDeDestinacao;
    }

    public Boolean getValidarValorNegativo() {
        return validarValorNegativo;
    }

    public void setValidarValorNegativo(Boolean validarValorNegativo) {
        this.validarValorNegativo = validarValorNegativo;
    }
}

