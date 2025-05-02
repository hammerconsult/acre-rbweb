package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoTaxasDiversas;


public class GeraReferenciaTaxaDiversa extends GeraReferencia<CalculoTaxasDiversas> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getNumero() != null) {
            referencia += " Processo: " + calculo.getNumeroFormatado();
        }
        return referencia;
    }

}
