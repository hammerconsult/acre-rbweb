package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.interfaces.CalculoAlvara;


public class GeraReferenciaAlvara extends GeraReferencia<CalculoAlvara> {

    @Override
    protected String getReferencia() {
        return "Exercício: " + calculo.getAlvara().getExercicio().getAno()
                + " Tipo: " + (calculo.getAlvara().getProvisorio() ? " Provisório " : " ")
                + calculo.getAlvara().getTipoAlvara().getDescricao();
    }


}
