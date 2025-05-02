package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoBloqueioJudicial;

public class GeraReferenciaBloqueioJudicial extends GeraReferencia<CalculoBloqueioJudicial> {
    @Override
    protected String getReferencia() {
        return calculo.getReferencia() != null ? calculo.getReferencia() : "";
    }
}
