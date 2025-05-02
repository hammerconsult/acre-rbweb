package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * Created by Wellington on 24/11/2015.
 */
public class AssistenteRelacaoLancamentoFiscalizacaoSecretaria extends AssistenteRelacaoLancamentoTributario {

    private Boolean somenteTotalizadorSecretaria;
    private List<VOAgrupamentoPorSituacao> resultadoPorSecretaria;

    public AssistenteRelacaoLancamentoFiscalizacaoSecretaria(AssistenteRelacaoLancamentoTributario assistente, Boolean totalizadoresPorSecretaria) {
        setResultado(assistente.getResultado());
        setResultadoPorSituacao(assistente.getResultadoPorSituacao());
        setSomenteTotalizador(assistente.getSomenteTotalizador());
        this.somenteTotalizadorSecretaria = totalizadoresPorSecretaria;
    }

    public Boolean getSomenteTotalizadorSecretaria() {
        return somenteTotalizadorSecretaria;
    }

    public void setSomenteTotalizadorSecretaria(Boolean somenteTotalizadorSecretaria) {
        this.somenteTotalizadorSecretaria = somenteTotalizadorSecretaria;
    }

    public List<VOAgrupamentoPorSituacao> getResultadoPorSecretaria() {
        return resultadoPorSecretaria;
    }

    public void setResultadoPorSecretaria(List<VOAgrupamentoPorSituacao> resultadoPorSecretaria) {
        this.resultadoPorSecretaria = resultadoPorSecretaria;
    }
}
