package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOIsencaoBci implements Serializable {
    private Long id;
    private String inicioVigencia;
    private String fimVigencia;
    private String tipoLancamento;
    private String situacao;
    private String agrupador;

    public VOIsencaoBci() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(String inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(String fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }
}
