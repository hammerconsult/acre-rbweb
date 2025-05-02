package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class GraficoAcompanhamentoDiarioDTO implements Serializable {
    private String[] labels;
    private Integer[] dados;

    public GraficoAcompanhamentoDiarioDTO() {
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public Integer[] getDados() {
        return dados;
    }

    public void setDados(Integer[] dados) {
        this.dados = dados;
    }
}
