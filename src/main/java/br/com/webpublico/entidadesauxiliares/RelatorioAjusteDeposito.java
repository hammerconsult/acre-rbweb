package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mateus on 10/08/17.
 */
public class RelatorioAjusteDeposito {

    private String numero;
    private Date dataAjuste;
    private String tipoAjuste;
    private String tipoLancamento;
    private BigDecimal valor;
    private String historico;
    private String pessoa;
    private String classe;
    private String fonteDeRecursos;
    private String contaExtra;
    private String unidade;
    private String orgao;
    private String unidadeGestora;

    public RelatorioAjusteDeposito() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataAjuste() {
        return dataAjuste;
    }

    public void setDataAjuste(Date dataAjuste) {
        this.dataAjuste = dataAjuste;
    }

    public String getTipoAjuste() {
        return tipoAjuste;
    }

    public void setTipoAjuste(String tipoAjuste) {
        this.tipoAjuste = tipoAjuste;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
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

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
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

    public String getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(String contaExtra) {
        this.contaExtra = contaExtra;
    }
}
