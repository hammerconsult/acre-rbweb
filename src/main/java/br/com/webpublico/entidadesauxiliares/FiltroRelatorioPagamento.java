package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * @author Mateus
 * @since 18/02/2016 14:48
 */
public class FiltroRelatorioPagamento {

    private Boolean pesquisouUg;
    private List<ParametrosRelatorios> parametros;
    private String apresentacao;
    private Boolean filtrarContaExtra;
    private String ordenacao;

    public FiltroRelatorioPagamento() {
    }

    public Boolean getPesquisouUg() {
        return pesquisouUg;
    }

    public void setPesquisouUg(Boolean pesquisouUg) {
        this.pesquisouUg = pesquisouUg;
    }

    public List<ParametrosRelatorios> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametrosRelatorios> parametros) {
        this.parametros = parametros;
    }

    public String getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(String apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Boolean getFiltrarContaExtra() {
        return filtrarContaExtra;
    }

    public void setFiltrarContaExtra(Boolean filtrarContaExtra) {
        this.filtrarContaExtra = filtrarContaExtra;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }
}
