package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoContribuicaoMelhoria;


public class GeraReferenciaContribuicaoMelhoria extends GeraReferencia<CalculoContribuicaoMelhoria> {


    public String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if ((calculo).getContribuicaoMelhoria() != null) {
            referencia += " Edital: " + (calculo).getContribuicaoMelhoria().getEdital().getCodigo();
        }
        return referencia;
    }

}
