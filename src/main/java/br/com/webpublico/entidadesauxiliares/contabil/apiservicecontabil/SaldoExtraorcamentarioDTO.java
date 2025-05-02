package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.TipoOperacao;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SaldoExtraorcamentarioDTO {
    private Long id;
    private LocalDate dataSaldo;
    private BigDecimal valor;
    private Long idContaExtraorcamentaria;
    private Long idFonteDeRecursos;
    private Long idUnidadeOrganizacional;
    private TipoOperacao tipoOperacao;

    private Long idContaDeDestinacao;
    private Boolean realizarValidacoes;
    private String classeOrigem;
    private String idOrigem;

    public SaldoExtraorcamentarioDTO() {
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

    public Long getIdContaExtraorcamentaria() {
        return idContaExtraorcamentaria;
    }

    public void setIdContaExtraorcamentaria(Long idContaExtraorcamentaria) {
        this.idContaExtraorcamentaria = idContaExtraorcamentaria;
    }

    public Long getIdFonteDeRecursos() {
        return idFonteDeRecursos;
    }

    public void setIdFonteDeRecursos(Long idFonteDeRecursos) {
        this.idFonteDeRecursos = idFonteDeRecursos;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public Long getIdContaDeDestinacao() {
        return idContaDeDestinacao;
    }

    public void setIdContaDeDestinacao(Long idContaDeDestinacao) {
        this.idContaDeDestinacao = idContaDeDestinacao;
    }

    public Boolean getRealizarValidacoes() {
        return realizarValidacoes;
    }

    public void setRealizarValidacoes(Boolean realizarValidacoes) {
        this.realizarValidacoes = realizarValidacoes;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }
}

