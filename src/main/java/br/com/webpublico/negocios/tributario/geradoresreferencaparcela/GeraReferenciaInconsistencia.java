package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoIPTU;
import br.com.webpublico.entidades.CalculoInconsistencia;


public class GeraReferenciaInconsistencia extends GeraReferencia<CalculoInconsistencia> {

    @Override
    protected String getReferencia() {
        return "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno() + "/ " + calculo.getItemLoteBaixa().getDamInformado();  //To change body of implemented methods use File | Settings | File Templates.
    }

}
