package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CancelamentoParcelamento;

public class GeraReferenciaCancelamentoParcelamento extends GeraReferencia<CancelamentoParcelamento> {

    public String getReferencia() {
        return calculo.getReferencia();
    }

}
