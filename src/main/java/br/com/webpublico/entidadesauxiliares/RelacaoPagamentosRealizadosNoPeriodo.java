package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class RelacaoPagamentosRealizadosNoPeriodo {
    private String codigoOrgao;
    private String codigoUnidade;
    private String codigoUnidadeGestora;
    private String descricaoOrgao;
    private String descricaoUnidade;
    private String descricaoUnidadeGestora;
    private String numeroPagamento;
    private String credor;
    private String cpfCnpj;
    private Date data;
    private String fonte;
    private String competencia;
    private String tipoDaDespesa;
    private String descricaoPrograma;
    private BigDecimal valorPago;
    private String projetoAtividade;

    public RelacaoPagamentosRealizadosNoPeriodo() {
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getCodigoUnidadeGestora() {
        return codigoUnidadeGestora;
    }

    public void setCodigoUnidadeGestora(String codigoUnidadeGestora) {
        this.codigoUnidadeGestora = codigoUnidadeGestora;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getDescricaoUnidadeGestora() {
        return descricaoUnidadeGestora;
    }

    public void setDescricaoUnidadeGestora(String descricaoUnidadeGestora) {
        this.descricaoUnidadeGestora = descricaoUnidadeGestora;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public String getCredor() {
        return credor;
    }

    public void setCredor(String credor) {
        this.credor = credor;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getTipoDaDespesa() {
        return tipoDaDespesa;
    }

    public void setTipoDaDespesa(String tipoDaDespesa) {
        this.tipoDaDespesa = tipoDaDespesa;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(String projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getDescricaoPrograma() {
        return descricaoPrograma;
    }

    public void setDescricaoPrograma(String descricaoPrograma) {
        this.descricaoPrograma = descricaoPrograma;
    }
}
