package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoPagamentoSubvencao;

/**
 * Created by tharlyson on 06/12/19.
 */
public class GeraReferenciaSubvencao extends GeraReferencia<CalculoPagamentoSubvencao> {

    @Override
    protected String getReferencia() {
        if (calculo != null && !calculo.getReferencia().isEmpty()) {
            return calculo.getReferencia();
        }
        return "";
    }
}
