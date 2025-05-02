package br.com.webpublico.entidadesauxiliares;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VOCdaProtesto implements Serializable {
    private Long id;
    private String nossoNumero;
    private Long idCda;
    private String numeroCDA;
    private String situacaoParcela;

    private VOCdaProtesto() {
    }

    public VOCdaProtesto(Long id, String nossoNumero, Long idCda, String numeroCDA, String situacaoParcela) {
        this.id = id;
        this.nossoNumero = nossoNumero;
        this.numeroCDA = numeroCDA;
        this.idCda = idCda;
        this.situacaoParcela = situacaoParcela;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public String getNumeroCDA() {
        return numeroCDA;
    }

    public void setNumeroCDA(String numeroCDA) {
        this.numeroCDA = numeroCDA;
    }

    public String getSituacaoParcela() {
        return !Strings.isNullOrEmpty(situacaoParcela) ? situacaoParcela : "Situação não recebida";
    }

    public void setSituacaoParcela(String situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    @JsonIgnore
    public Long getIdCda() {
        return idCda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOCdaProtesto that = (VOCdaProtesto) o;
        return Objects.equal(nossoNumero, that.nossoNumero) && Objects.equal(numeroCDA, that.numeroCDA);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nossoNumero, numeroCDA);
    }
}
