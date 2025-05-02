package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoContaCorrente;


public class GeraReferenciaContaCorrente extends GeraReferencia<CalculoContaCorrente> {

    @Override
    protected String getReferencia() {
        return calculo.getReferencia();
    }

}
