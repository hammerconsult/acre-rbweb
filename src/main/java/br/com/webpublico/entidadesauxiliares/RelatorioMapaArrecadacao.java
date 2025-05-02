package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class RelatorioMapaArrecadacao implements Comparable<RelatorioMapaArrecadacao> {

    private Date dataArrecadacao;
    private Date dataMovimento;
    private String agenteArrecadador;
    private String banco;
    private String formaPagamento;
    private List<MapaArrecadacao> arrecadacoes;
    private List<MapaArrecadacao> acrescimos;
    private List<MapaArrecadacao> descontos;

    public RelatorioMapaArrecadacao() {
        arrecadacoes = Lists.newArrayList();
        acrescimos = Lists.newArrayList();
        descontos = Lists.newArrayList();
    }

    public RelatorioMapaArrecadacao(Date dataArrecadacao, String agenteArrecadador) {
        this();
        this.dataArrecadacao = dataArrecadacao;
        this.agenteArrecadador = agenteArrecadador;
    }

    public Date getDataArrecadacao() {
        return dataArrecadacao;
    }

    public void setDataArrecadacao(Date dataArrecadacao) {
        this.dataArrecadacao = dataArrecadacao;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getAgenteArrecadador() {
        return agenteArrecadador;
    }

    public void setAgenteArrecadador(String agenteArrecadador) {
        this.agenteArrecadador = agenteArrecadador;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<MapaArrecadacao> getArrecadacoes() {
        return arrecadacoes;
    }

    public void setArrecadacoes(List<MapaArrecadacao> arrecadacoes) {
        this.arrecadacoes = arrecadacoes;
    }

    public List<MapaArrecadacao> getAcrescimos() {
        return acrescimos;
    }

    public void setAcrescimos(List<MapaArrecadacao> acrescimos) {
        this.acrescimos = acrescimos;
    }

    public List<MapaArrecadacao> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<MapaArrecadacao> descontos) {
        this.descontos = descontos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatorioMapaArrecadacao)) return false;

        RelatorioMapaArrecadacao that = (RelatorioMapaArrecadacao) o;


        if (agenteArrecadador != null ? !agenteArrecadador.equals(that.agenteArrecadador) : that.agenteArrecadador != null)
            return false;
        if (dataArrecadacao != null ? !dataArrecadacao.equals(that.dataArrecadacao) : that.dataArrecadacao != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dataArrecadacao != null ? dataArrecadacao.hashCode() : 0;
        result = 31 * result + (agenteArrecadador != null ? agenteArrecadador.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(RelatorioMapaArrecadacao o) {
        int i = 0;
        if (dataArrecadacao != null && o.getDataArrecadacao() != null) {
            i = dataArrecadacao.compareTo(o.getDataArrecadacao());
        }
        if (i == 0 && agenteArrecadador != null && o.getAgenteArrecadador() != null) {
            i = agenteArrecadador.compareTo(o.getAgenteArrecadador());
        }
        return i;
    }
}
