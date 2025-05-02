package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoRendas;


public class GeraReferenciaRendas extends GeraReferencia<CalculoRendas> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getContrato() != null) {
            referencia += " Contrato: " + calculo.getContrato().getNumeroContrato();
        }
        return referencia;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
