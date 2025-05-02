package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiltroCupomSorteioNfse implements Serializable {

    private Long idCampanha;
    private Integer numeroSorteio;
    private Date inicioEmissaoNotaFiscal;
    private Date fimEmissaoNotaFiscal;
    private String numeroCupom;
    private Date emissaoNfse;
    private String numeroNfse;
    private String cpfCnpjPrestador;
    private String nomePrestador;
    private String cpfCnpjTomador;
    private String nomeTomador;
    private Boolean premiado;

    private int quantidadeTotal;
    private int quantidadePorPagina;
    private int indexInicialDaPesquisa;

    private List<DetalheCupomSorteioNfseDTO> cupons;

    public FiltroCupomSorteioNfse() {
        quantidadeTotal = 0;
        quantidadePorPagina = 10;
        indexInicialDaPesquisa = 0;
        cupons = new ArrayList<>();
    }

    public Long getIdCampanha() {
        return idCampanha;
    }

    public void setIdCampanha(Long idCampanha) {
        this.idCampanha = idCampanha;
    }

    public Integer getNumeroSorteio() {
        return numeroSorteio;
    }

    public void setNumeroSorteio(Integer numeroSorteio) {
        this.numeroSorteio = numeroSorteio;
    }

    public Date getInicioEmissaoNotaFiscal() {
        return inicioEmissaoNotaFiscal;
    }

    public void setInicioEmissaoNotaFiscal(Date inicioEmissaoNotaFiscal) {
        this.inicioEmissaoNotaFiscal = inicioEmissaoNotaFiscal;
    }

    public Date getFimEmissaoNotaFiscal() {
        return fimEmissaoNotaFiscal;
    }

    public void setFimEmissaoNotaFiscal(Date fimEmissaoNotaFiscal) {
        this.fimEmissaoNotaFiscal = fimEmissaoNotaFiscal;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }

    public Date getEmissaoNfse() {
        return emissaoNfse;
    }

    public void setEmissaoNfse(Date emissaoNfse) {
        this.emissaoNfse = emissaoNfse;
    }

    public String getNumeroNfse() {
        return numeroNfse;
    }

    public void setNumeroNfse(String numeroNfse) {
        this.numeroNfse = numeroNfse;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public String getNomePrestador() {
        return nomePrestador;
    }

    public void setNomePrestador(String nomePrestador) {
        this.nomePrestador = nomePrestador;
    }

    public String getCpfCnpjTomador() {
        return cpfCnpjTomador;
    }

    public void setCpfCnpjTomador(String cpfCnpjTomador) {
        this.cpfCnpjTomador = cpfCnpjTomador;
    }

    public String getNomeTomador() {
        return nomeTomador;
    }

    public void setNomeTomador(String nomeTomador) {
        this.nomeTomador = nomeTomador;
    }

    public Boolean getPremiado() {
        return premiado;
    }

    public void setPremiado(Boolean premiado) {
        this.premiado = premiado;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadePorPagina() {
        return quantidadePorPagina;
    }

    public void setQuantidadePorPagina(int quantidadePorPagina) {
        this.quantidadePorPagina = quantidadePorPagina;
    }

    public int getIndexInicialDaPesquisa() {
        return indexInicialDaPesquisa;
    }

    public void setIndexInicialDaPesquisa(int indexInicialDaPesquisa) {
        this.indexInicialDaPesquisa = indexInicialDaPesquisa;
    }

    public List<DetalheCupomSorteioNfseDTO> getCupons() {
        return cupons;
    }

    public void setCupons(List<DetalheCupomSorteioNfseDTO> cupons) {
        this.cupons = cupons;
    }
}
