package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoMultaAcessoria;
import br.com.webpublico.util.StringUtil;

public class GeraReferenciaMultaAcessoria extends GeraReferencia<CalculoMultaAcessoria>{

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getProcessoCalculoMultaAcessoria().getCalculoISS() != null &&
                calculo.getProcessoCalculoMultaAcessoria().getCalculoISS().getProcessoCalculoISS().getMesReferencia() != null
                && calculo.getProcessoCalculoMultaAcessoria().getCalculoISS().getProcessoCalculoISS().getMesReferencia() > 0) {

            referencia += " Mês: " + StringUtil.preencheString(calculo.getProcessoCalculoMultaAcessoria().getCalculoISS().getProcessoCalculoISS().getMesReferencia()+ "", 2, '0');
        }
        if (calculo.getSubDivida() != null && calculo.getProcessoCalculoMultaAcessoria().getCalculoISS() != null) {
            referencia += " Seq.: " + calculo.getProcessoCalculoMultaAcessoria().getCalculoISS().getSequenciaLancamento();
        }
        return referencia;
    }

}
