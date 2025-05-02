package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 18/10/2017.
 */
public class RelatorioPatrimonioLiquido {

    private Date dataLancamento;
    private String numero;
    private String tipoLancamento;
    private String operacao;
    private String eventoContabil;
    private String pessoa;
    private String classe;
    private String historico;
    private BigDecimal valor;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoOrgao;
    private String descricaoOrgao;

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(String eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
}
