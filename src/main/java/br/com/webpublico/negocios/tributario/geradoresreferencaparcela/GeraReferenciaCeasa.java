package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.interfaces.CalculoCeasa;


public class GeraReferenciaCeasa extends GeraReferencia<CalculoCeasa> {


    public String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if ((calculo).getContrato() != null) {
            referencia += " Contrato: " + (calculo).getContrato().getNumeroContrato();
        }
        return referencia;
    }

}
