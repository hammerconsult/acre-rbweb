package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.ApresentacaoRelatorio;

import java.util.List;

public class FiltrosRelatorioDemonstrativoReceitaPorNatureza {

    private ApresentacaoRelatorio apresentacao;
    private Boolean exibeFonteRecurso;
    private UnidadeGestora unidadeGestora;
    private List<ParametrosRelatorios> parametros;

    public FiltrosRelatorioDemonstrativoReceitaPorNatureza() {
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Boolean getExibeFonteRecurso() {
        return exibeFonteRecurso;
    }

    public void setExibeFonteRecurso(Boolean exibeFonteRecurso) {
        this.exibeFonteRecurso = exibeFonteRecurso;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<ParametrosRelatorios> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametrosRelatorios> parametros) {
        this.parametros = parametros;
    }
}
