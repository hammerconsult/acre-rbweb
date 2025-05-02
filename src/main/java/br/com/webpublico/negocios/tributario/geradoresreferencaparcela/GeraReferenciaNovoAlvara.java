package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoAlvara;

public class GeraReferenciaNovoAlvara extends GeraReferencia<CalculoAlvara> {

    @Override
    protected String getReferencia() {
        return "Exercício: " + calculo.getProcessoCalculoAlvara().getExercicio().getAno() + " "
            + calculo.getTipoAlvara().getDescricao();
    }

}
