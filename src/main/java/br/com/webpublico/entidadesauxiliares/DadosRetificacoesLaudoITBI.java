package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

public class DadosRetificacoesLaudoITBI implements Serializable {
    private String motivoRetificacao;
    private Integer numeroRetificacao;
    private Date dataRetificacao;

    public DadosRetificacoesLaudoITBI() {
    }

    public String getMotivoRetificacao() {
        return motivoRetificacao;
    }

    public void setMotivoRetificacao(String motivoRetificacao) {
        this.motivoRetificacao = motivoRetificacao;
    }

    public Integer getNumeroRetificacao() {
        return numeroRetificacao;
    }

    public void setNumeroRetificacao(Integer numeroRetificacao) {
        this.numeroRetificacao = numeroRetificacao;
    }

    public Date getDataRetificacao() {
        return dataRetificacao;
    }

    public void setDataRetificacao(Date dataRetificacao) {
        this.dataRetificacao = dataRetificacao;
    }
}
