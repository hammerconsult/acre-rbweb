package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

public class VOCdaSimulacaoTransferenciaBCI implements Serializable {
    private Long id;
    private Long numero;
    private Integer exercicio;
    private String situacaoParcela;
    private String situacaoJudicial;
    private Date dataCertidao;
    private String agrupador;

    public VOCdaSimulacaoTransferenciaBCI() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(String situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public String getSituacaoJudicial() {
        return situacaoJudicial;
    }

    public void setSituacaoJudicial(String situacaoJudicial) {
        this.situacaoJudicial = situacaoJudicial;
    }

    public Date getDataCertidao() {
        return dataCertidao;
    }

    public void setDataCertidao(Date dataCertidao) {
        this.dataCertidao = dataCertidao;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }
}
