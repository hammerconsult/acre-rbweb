package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.Date;

public class DetalheCupomSorteioNfseDTO implements Serializable {

    private Long id;
    private String numeroCupom;
    private Boolean premiado;
    private Integer numeroNotaFiscal;
    private Date emissaoNotaFiscal;
    private String prestador;
    private String tomador;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public Boolean getPremiado() {
        return premiado;
    }

    public void setPremiado(Boolean premiado) {
        this.premiado = premiado;
    }

    public Integer getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Date getEmissaoNotaFiscal() {
        return emissaoNotaFiscal;
    }

    public void setEmissaoNotaFiscal(Date emissaoNotaFiscal) {
        this.emissaoNotaFiscal = emissaoNotaFiscal;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }
}
