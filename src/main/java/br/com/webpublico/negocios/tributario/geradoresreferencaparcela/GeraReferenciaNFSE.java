package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoIPTU;
import br.com.webpublico.entidades.CalculoNfse;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.StringUtil;


public class GeraReferenciaNFSE extends GeraReferencia<CalculoNfse> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getMesDeReferencia() != null && calculo.getMesDeReferencia() > 0) {
            referencia += " Mês: " + StringUtil.preencheString(calculo.getMesDeReferencia() + "", 2, '0');
        }
        if (calculo.getSubDivida() != null) {
            referencia += " Seq.: " + StringUtil.preencheString(calculo.getSubDivida() + "", 2, '0');
        }
        return referencia;
    }

}
