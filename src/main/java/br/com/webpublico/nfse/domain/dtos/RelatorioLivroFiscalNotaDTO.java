package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by rodolfo on 23/07/18.
 */
public class RelatorioLivroFiscalNotaDTO implements Comparable<RelatorioLivroFiscalNotaDTO> {
    private Long idNota;
    private Integer numeroNota;
    private Date dataNota;
    private String tipoNota;
    private Boolean simplesNacional;
    private Boolean mei;
    private String exigibilidadeIss;
    private String situacaoNota;
    private Boolean issRetido;
    private BigDecimal totalBaseCalculo;
    private BigDecimal totalISS;
    private String nomeTtomador;
    private String cpfCnpjTtomador;
    private String municipio;
    private String uf;
    private List<RelatorioLivroFiscalServicoDTO> servicos;

    public Integer getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(Integer numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Date getDataNota() {
        return dataNota;
    }

    public void setDataNota(Date dataNota) {
        this.dataNota = dataNota;
    }

    public String getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(String tipoNota) {
        this.tipoNota = tipoNota;
    }

    public Boolean getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
    }

    public Boolean getMei() {
        return mei;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }

    public String getExigibilidadeIss() {
        return exigibilidadeIss;
    }

    public void setExigibilidadeIss(String exigibilidadeIss) {
        this.exigibilidadeIss = exigibilidadeIss;
    }

    public String getSituacaoNota() {
        return situacaoNota;
    }

    public void setSituacaoNota(String situacaoNota) {
        this.situacaoNota = situacaoNota;
    }

    public Boolean getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getTotalBaseCalculo() {
        return totalBaseCalculo;
    }

    public void setTotalBaseCalculo(BigDecimal totalBaseCalculo) {
        this.totalBaseCalculo = totalBaseCalculo;
    }

    public BigDecimal getTotalISS() {
        return totalISS;
    }

    public void setTotalISS(BigDecimal totalISS) {
        this.totalISS = totalISS;
    }

    public String getNomeTtomador() {
        return nomeTtomador;
    }

    public void setNomeTtomador(String nomeTtomador) {
        this.nomeTtomador = nomeTtomador;
    }

    public String getCpfCnpjTtomador() {
        return cpfCnpjTtomador;
    }

    public void setCpfCnpjTtomador(String cpfCnpjTtomador) {
        this.cpfCnpjTtomador = cpfCnpjTtomador;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public List<RelatorioLivroFiscalServicoDTO> getServicos() {
        return servicos;
    }

    public void setServicos(List<RelatorioLivroFiscalServicoDTO> servicos) {
        this.servicos = servicos;
    }

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }

    @Override
    public int compareTo(RelatorioLivroFiscalNotaDTO o) {
        if (this.numeroNota == null || o == null || o.getNumeroNota() == null) {
            return 0;
        }
        return this.numeroNota.compareTo(o.getNumeroNota());
    }
}
