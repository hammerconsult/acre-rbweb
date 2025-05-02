package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoNFSAvulsa;


public class GeraReferenciaNFSAvulsa extends GeraReferencia<CalculoNFSAvulsa> {
    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getNfsAvulsa() != null) {
            referencia += " Nota: " + calculo.getNfsAvulsa().getNumeroCompleto();
        }
        return referencia;
    }

}
