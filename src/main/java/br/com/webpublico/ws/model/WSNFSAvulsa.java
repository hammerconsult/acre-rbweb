package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.enums.TipoTomadorPrestadorNF;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class WSNFSAvulsa implements Serializable {
    private String cpfCnpj;
    private Long id;
    private Integer ano;
    private Long numero;
    private Date dataEmissao;
    private Date dataNota;
    private String prestador;
    private String cpfCnpjPrestador;
    private String tomador;
    private String cpfCnpjTomador;
    protected BigDecimal percentualIss;
    private BigDecimal valorTotal;
    private BigDecimal valorIss;
    private String situacaoDaNota;
    private Boolean cancelada;
    private String codigoAutenticidade;
    private String origemNota;
    private Boolean damEmAberto;

    public WSNFSAvulsa() {
    }

    public WSNFSAvulsa(NFSAvulsa nota) {
        this.id = nota.getId();
        this.ano = nota.getExercicio().getAno();
        this.numero = nota.getNumero();
        this.dataEmissao = nota.getEmissao();
        this.dataNota = nota.getDataNota();
        if (TipoTomadorPrestadorNF.ECONOMICO.equals(nota.getTipoPrestadorNF())) {
            this.prestador = nota.getCmcPrestador().getPessoa().getNome();
            this.cpfCnpjPrestador = nota.getCmcPrestador().getPessoa().getCpf_Cnpj();
        } else if (TipoTomadorPrestadorNF.PESSOA.equals(nota.getTipoPrestadorNF()) && nota.getPrestador() != null) {

            this.prestador = nota.getPrestador().getNome();
            this.cpfCnpjPrestador = nota.getPrestador().getCpf_Cnpj();
        }
        if (TipoTomadorPrestadorNF.ECONOMICO.equals(nota.getTipoTomadorNF())) {
            this.tomador = nota.getCmcTomador().getPessoa().getNome();
            this.cpfCnpjTomador = nota.getCmcTomador().getPessoa().getCpf_Cnpj();
        } else if (TipoTomadorPrestadorNF.PESSOA.equals(nota.getTipoTomadorNF())) {
            this.tomador = nota.getTomador().getNome();
            this.cpfCnpjTomador = nota.getTomador().getCpf_Cnpj();
        }
        this.valorTotal = nota.getValorServicos();
        this.percentualIss = nota.getValorIss();
        this.valorIss = nota.getValorTotalIss();
        this.situacaoDaNota = nota.getSituacao().getDescricao();
        this.cancelada = nota.getSituacao().equals(NFSAvulsa.Situacao.CANCELADA);
        this.origemNota = nota.getOrigemNotaAvulsa() != null ? nota.getOrigemNotaAvulsa().name() : null;
        this.setCodigoAutenticidade(nota.getAutenticidade());
    }

    public String getCodigoAutenticidade() {
        return codigoAutenticidade;
    }

    public void setCodigoAutenticidade(String codigoAutenticidade) {
        this.codigoAutenticidade = codigoAutenticidade;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataNota() {
        return dataNota;
    }

    public void setDataNota(Date dataNota) {
        this.dataNota = dataNota;
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public String getCpfCnpjTomador() {
        return cpfCnpjTomador;
    }

    public void setCpfCnpjTomador(String cpfCnpjTomador) {
        this.cpfCnpjTomador = cpfCnpjTomador;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSituacaoDaNota() {
        return situacaoDaNota;
    }

    public void setSituacaoDaNota(String situacaoDaNota) {
        this.situacaoDaNota = situacaoDaNota;
    }

    public Boolean getCancelada() {
        return cancelada;
    }

    public void setCancelada(Boolean cancelada) {
        this.cancelada = cancelada;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public BigDecimal getPercentualIss() {
        return percentualIss;
    }

    public void setPercentualIss(BigDecimal percentualIss) {
        this.percentualIss = percentualIss;
    }

    public String getOrigemNota() {
        return origemNota;
    }

    public void setOrigemNota(String origemNota) {
        this.origemNota = origemNota;
    }

    public Boolean getDamEmAberto() {
        return damEmAberto != null ? damEmAberto : Boolean.FALSE;
    }

    public void setDamEmAberto(Boolean damEmAberto) {
        this.damEmAberto = damEmAberto;
    }
}
