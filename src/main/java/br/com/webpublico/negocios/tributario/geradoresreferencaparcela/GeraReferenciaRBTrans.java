package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoRBTrans;

public class GeraReferenciaRBTrans extends GeraReferencia<CalculoRBTrans> {
    @Override
    protected String getReferencia() {
        if (calculo != null) {
            if (calculo.getTipoCalculoRBTRans() != null) {
                return calculo.getTipoCalculoRBTRans().getDescricao();
            }
        }
        return "";
    }
}
