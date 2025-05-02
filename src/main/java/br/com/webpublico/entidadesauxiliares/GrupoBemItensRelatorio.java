package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Mateus on 09/04/2015.
 */
public class GrupoBemItensRelatorio {
    private String numero;
    private Date dataBem;
    private String tipoLancamento;
    private String historico;
    private BigDecimal valor;
    private String tipoGrupo;
    private String operacao;
    private String ingresso;
    private String tipoBaixa;
    private String grupo;
    private String evento;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public GrupoBemItensRelatorio() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataBem() {
        return dataBem;
    }

    public void setDataBem(Date dataBem) {
        this.dataBem = dataBem;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
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

    public String getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(String tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getIngresso() {
        return ingresso;
    }

    public void setIngresso(String ingresso) {
        this.ingresso = ingresso;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
