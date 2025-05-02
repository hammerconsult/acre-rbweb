package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoPagamentoJudicial;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 01/06/15
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public class GeraReferenciaCompensacao extends GeraReferencia<CalculoPagamentoJudicial> {

    @Override
    protected String getReferencia() {
        if (calculo != null && !calculo.getReferencia().isEmpty()) {
            return calculo.getReferencia();
        }
        return "";
    }
}
