package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;

import java.io.Serializable;
import java.util.Date;

public class FiltroResiduoArrecadacao implements Serializable {

    private Pessoa contribuinte;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private String situacao;

    public FiltroResiduoArrecadacao() {
        situacao = "0";
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
