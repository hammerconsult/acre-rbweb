package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoAlvaraConstrucaoHabitese;


public class GeraReferenciaAlvaraConstrucaoHabitese extends GeraReferencia<CalculoAlvaraConstrucaoHabitese> {

    @Override
    protected String getReferencia() {
        if (calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao() != null) {
            return "Processo: " + calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao().getCodigo()
                + " Exercício: " + calculo.getProcCalcAlvaraConstruHabit().getAlvaraConstrucao().getExercicio().getAno();
        } else {
            return "Processo: " + calculo.getProcCalcAlvaraConstruHabit().getHabitese().getCodigo()
                + " Exercício: " + calculo.getProcCalcAlvaraConstruHabit().getHabitese().getExercicio().getAno();
        }
    }


}
