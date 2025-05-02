package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoFiscalizacaoRBTrans;

public class GeraReferenciaFiscalizacaoRBTrans extends GeraReferencia<CalculoFiscalizacaoRBTrans> {
    @Override
    protected String getReferencia() {
        if (calculo != null) {
            return "Processo de Autuação: " + calculo.getProcessoCalculoFiscalizacaoRBTrans().getFiscalizacaoRBTrans().getAutuacaoFiscalizacao().getCodigo().toString();
        }
    return "";
    }
}
