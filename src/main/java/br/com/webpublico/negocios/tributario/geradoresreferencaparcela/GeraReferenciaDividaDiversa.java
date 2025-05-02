package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoDividaDiversa;


public class GeraReferenciaDividaDiversa extends GeraReferencia<CalculoDividaDiversa> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        referencia += " Processo: " + calculo.getNumeroFormatado();
        if (calculo.getTipoDividaDiversa() != null) {
            referencia += " Tipo: " + calculo.getTipoDividaDiversa().getDescricao();
        }
        return referencia;
    }

}
