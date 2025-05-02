package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wellington on 24/11/2015.
 */
public class AssistenteRelacaoLancamentoTributario implements Serializable {

    private Boolean somenteTotalizador;
    private List<? extends AbstractVOConsultaLancamento> resultado;
    private List<VOAgrupamentoPorSituacao> resultadoPorSituacao;

    public AssistenteRelacaoLancamentoTributario() {
    }

    public Boolean getSomenteTotalizador() {
        return somenteTotalizador;
    }

    public void setSomenteTotalizador(Boolean somenteTotalizador) {
        this.somenteTotalizador = somenteTotalizador;
    }

    public AssistenteRelacaoLancamentoTributario(List<? extends AbstractVOConsultaLancamento> resultado, List<VOAgrupamentoPorSituacao> resultadoPorSituacao) {
        this.setResultado(resultado);
        this.setResultadoPorSituacao(resultadoPorSituacao);
    }

    public List<? extends AbstractVOConsultaLancamento> getResultado() {
        return resultado;
    }

    public void setResultado(List<? extends AbstractVOConsultaLancamento> resultado) {
        this.resultado = resultado;
    }

    public List<VOAgrupamentoPorSituacao> getResultadoPorSituacao() {
        return resultadoPorSituacao;
    }

    public void setResultadoPorSituacao(List<VOAgrupamentoPorSituacao> resultadoPorSituacao) {
        this.resultadoPorSituacao = resultadoPorSituacao;
    }


}
