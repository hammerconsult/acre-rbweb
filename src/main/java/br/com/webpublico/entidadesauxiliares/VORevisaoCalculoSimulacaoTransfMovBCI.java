package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VORevisaoCalculoSimulacaoTransfMovBCI implements Serializable {
    private Long id;
    private String dataCriacao;
    private Long codigo;
    private String processo;
    private String agrupador;

    public VORevisaoCalculoSimulacaoTransfMovBCI() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }
}
