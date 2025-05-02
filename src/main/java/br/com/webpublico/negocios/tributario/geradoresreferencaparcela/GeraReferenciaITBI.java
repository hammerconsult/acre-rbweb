package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoITBI;


public class GeraReferenciaITBI extends GeraReferencia<CalculoITBI> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        referencia += " Guia: " + calculo.getProcessoCalculoITBI().getCodigo();
        return referencia;
    }

}
