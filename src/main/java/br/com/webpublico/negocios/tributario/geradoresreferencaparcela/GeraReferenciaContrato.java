package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoContrato;

public class GeraReferenciaContrato extends GeraReferencia<CalculoContrato>{

    @Override
    protected String getReferencia() {
        return "Contrato: " + calculo.getContrato().getNumeroContrato() + " - " + calculo.getContrato().getNumeroAnoTermo();
    }
}
