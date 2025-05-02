package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.CalculoLancamentoOutorga;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.interfaces.CalculoCeasa;
import br.com.webpublico.interfaces.GeradorReferenciaParcela;


public class GeraReferenciaOutorga extends GeraReferencia<CalculoLancamentoOutorga> {

    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcCalcLancamentoOutorga() != null && calculo.getProcCalcLancamentoOutorga().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcCalcLancamentoOutorga().getExercicio().getAno();
        }
        if (calculo.getProcCalcLancamentoOutorga() != null && calculo.getProcCalcLancamentoOutorga().getLancamentoOutorga().getMes() != null) {
            referencia += " Mês: " + calculo.getProcCalcLancamentoOutorga().getLancamentoOutorga().getMes().getNumeroMes();

        }
        return referencia;
    }

}
