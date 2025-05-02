package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoIPTU;


public class GeraReferenciaIPTU extends GeraReferencia<CalculoIPTU> {

    @Override
    protected String getReferencia() {
        return "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();  //To change body of implemented methods use File | Settings | File Templates.
    }

}
