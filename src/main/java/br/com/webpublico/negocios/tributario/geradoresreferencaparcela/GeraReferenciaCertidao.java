package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoDoctoOficial;


public class GeraReferenciaCertidao extends GeraReferencia<CalculoDoctoOficial> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getSolicitacaoDoctoOficial() != null) {
            referencia += " Solicitação: " + calculo.getSolicitacaoDoctoOficial().getCodigo();
        }
        return referencia;
    }

}
