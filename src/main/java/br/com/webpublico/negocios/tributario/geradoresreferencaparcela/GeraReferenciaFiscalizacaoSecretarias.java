package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoProcFiscalizacao;


public class GeraReferenciaFiscalizacaoSecretarias extends GeraReferencia<CalculoProcFiscalizacao> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getProcessoFiscalizacao() != null) {
            referencia += " Processo: " + calculo.getProcessoFiscalizacao().getCodigo();
        }
        return referencia;
    }

}
