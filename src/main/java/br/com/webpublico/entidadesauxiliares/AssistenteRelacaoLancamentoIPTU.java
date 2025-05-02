package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * Created by Wellington on 24/11/2015.
 */
public class AssistenteRelacaoLancamentoIPTU extends AssistenteRelacaoLancamentoTributario {

    private Boolean somenteTotalizadorBairro;
    private List<VOAgrupamentoPorSituacao> resultadoPorBairro;

    public AssistenteRelacaoLancamentoIPTU(AssistenteRelacaoLancamentoTributario assistente, Boolean totalizadoresPorBairro) {
        setResultado(assistente.getResultado());
        setResultadoPorSituacao(assistente.getResultadoPorSituacao());
        setSomenteTotalizador(assistente.getSomenteTotalizador());
        this.somenteTotalizadorBairro = totalizadoresPorBairro;
    }

    public List<VOAgrupamentoPorSituacao> getResultadoPorBairro() {
        return resultadoPorBairro;
    }

    public void setResultadoPorBairro(List<VOAgrupamentoPorSituacao> resultadoPorBairro) {
        this.resultadoPorBairro = resultadoPorBairro;
    }

    public Boolean getSomenteTotalizadorBairro() {
        return somenteTotalizadorBairro;
    }
}
