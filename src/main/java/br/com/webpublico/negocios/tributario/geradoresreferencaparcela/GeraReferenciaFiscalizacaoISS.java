package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.interfaces.CalculoAutoInfracao;


public class GeraReferenciaFiscalizacaoISS extends GeraReferencia<CalculoAutoInfracao> {

    @Override
    protected String getReferencia() {
        if (calculo.getAutoInfracaoFiscal() != null &&
                calculo.getAutoInfracaoFiscal().getRegistro() != null &&
                calculo.getAutoInfracaoFiscal().getRegistro().getAcaoFiscal() != null &&
                calculo.getAutoInfracaoFiscal().getRegistro().getAcaoFiscal().getProgramacaoFiscal() != null) {
            return String.format("Exercício: %1s Processo: %2s", calculo.getAutoInfracaoFiscal().getAno(),
                calculo.getAutoInfracaoFiscal().getNumero());
        }
        return "";
    }

}
