package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.enums.TipoBalancete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 26/11/2014.
 */
public class BalanceteContabilFiltros {
    private ApresentacaoRelatorio apresentacao;
    private Boolean pesquisouUg;
    private List<ParametrosRelatorios> parametros;
    private Boolean exibirContaAuxiliar;
    private String dataInicial;
    private TipoBalancete tipoBalancete;
    private TipoBalancete tipoBalanceteFinal;
    private ClassificacaoContaAuxiliar classificacaoContaAuxiliar;

    public BalanceteContabilFiltros() {
        parametros = new ArrayList<>();
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
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

    public Boolean getExibirContaAuxiliar() {
        return exibirContaAuxiliar;
    }

    public void setExibirContaAuxiliar(Boolean exibirContaAuxiliar) {
        this.exibirContaAuxiliar = exibirContaAuxiliar;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    public TipoBalancete getTipoBalanceteFinal() {
        return tipoBalanceteFinal;
    }

    public void setTipoBalanceteFinal(TipoBalancete tipoBalanceteFinal) {
        this.tipoBalanceteFinal = tipoBalanceteFinal;
    }

    public ClassificacaoContaAuxiliar getClassificacaoContaAuxiliar() {
        return classificacaoContaAuxiliar;
    }

    public void setClassificacaoContaAuxiliar(ClassificacaoContaAuxiliar classificacaoContaAuxiliar) {
        this.classificacaoContaAuxiliar = classificacaoContaAuxiliar;
    }
}
