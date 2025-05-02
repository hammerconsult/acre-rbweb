package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 15/09/2014.
 */
public class RelatorioRestosAPagarItem {
    private String codigo;
    private String descricao;
    private Integer nivel;
    private BigDecimal restosNaoProcessadosPagos;
    private BigDecimal restosNaoProcessadosCancelados;
    private BigDecimal restosProcessadosPagos;
    private BigDecimal restosProcessadosCancelados;
    private BigDecimal inscritosProcessados;
    private BigDecimal inscritosNaoProcessados;
    private BigDecimal saldoAtualProcessados;
    private BigDecimal saldoAtualNaoProcessados;
    private Date dataEmpenho;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoFonte;
    private String descricaoFonte;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String pessoa;
    private String numeroEmpenho;

    public RelatorioRestosAPagarItem() {
        restosNaoProcessadosCancelados = BigDecimal.ZERO;
        restosNaoProcessadosPagos = BigDecimal.ZERO;
        restosProcessadosPagos = BigDecimal.ZERO;
        restosProcessadosCancelados = BigDecimal.ZERO;
        inscritosProcessados = BigDecimal.ZERO;
        inscritosNaoProcessados = BigDecimal.ZERO;
        saldoAtualProcessados = BigDecimal.ZERO;
        saldoAtualNaoProcessados = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getRestosNaoProcessadosPagos() {
        return restosNaoProcessadosPagos;
    }

    public void setRestosNaoProcessadosPagos(BigDecimal restosNaoProcessadosPagos) {
        this.restosNaoProcessadosPagos = restosNaoProcessadosPagos;
    }

    public BigDecimal getRestosNaoProcessadosCancelados() {
        return restosNaoProcessadosCancelados;
    }

    public void setRestosNaoProcessadosCancelados(BigDecimal restosNaoProcessadosCancelados) {
        this.restosNaoProcessadosCancelados = restosNaoProcessadosCancelados;
    }

    public BigDecimal getRestosProcessadosPagos() {
        return restosProcessadosPagos;
    }

    public void setRestosProcessadosPagos(BigDecimal restosProcessadosPagos) {
        this.restosProcessadosPagos = restosProcessadosPagos;
    }

    public BigDecimal getRestosProcessadosCancelados() {
        return restosProcessadosCancelados;
    }

    public void setRestosProcessadosCancelados(BigDecimal restosProcessadosCancelados) {
        this.restosProcessadosCancelados = restosProcessadosCancelados;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getInscritosProcessados() {
        return inscritosProcessados;
    }

    public void setInscritosProcessados(BigDecimal inscritosProcessados) {
        this.inscritosProcessados = inscritosProcessados;
    }

    public BigDecimal getInscritosNaoProcessados() {
        return inscritosNaoProcessados;
    }

    public void setInscritosNaoProcessados(BigDecimal inscritosNaoProcessados) {
        this.inscritosNaoProcessados = inscritosNaoProcessados;
    }

    public BigDecimal getSaldoAtualProcessados() {
        this.setSaldoAtualProcessados(this.getInscritosProcessados()
                .subtract(this.getRestosProcessadosPagos())
                .subtract(this.getRestosProcessadosCancelados()));
        return saldoAtualProcessados;
    }

    public void setSaldoAtualProcessados(BigDecimal saldoAtualProcessados) {
        this.saldoAtualProcessados = saldoAtualProcessados;
    }

    public BigDecimal getSaldoAtualNaoProcessados() {
        this.setSaldoAtualNaoProcessados(this.getInscritosNaoProcessados()
                .subtract(this.getRestosNaoProcessadosPagos())
                .subtract(this.getRestosNaoProcessadosCancelados()));
        return saldoAtualNaoProcessados;
    }

    public void setSaldoAtualNaoProcessados(BigDecimal saldoAtualNaoProcessados) {
        this.saldoAtualNaoProcessados = saldoAtualNaoProcessados;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public void setCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }
}
