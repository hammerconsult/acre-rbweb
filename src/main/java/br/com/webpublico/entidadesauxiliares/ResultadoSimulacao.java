/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.script.Resultado;

/**
 * @author peixe
 */
public class ResultadoSimulacao {

    private Resultado<Double> resultadoFormula;
    private Resultado<Double> resultadoValorIntegral;
    private Resultado<Double> resultadoReferencia;
    private Resultado<Boolean> resultadoRegra;
    private Resultado<Double> resultadoValorBaseDeCalculo;

    public Resultado<Double> getResultadoFormula() {
        return resultadoFormula;
    }

    public void setResultadoFormula(Resultado<Double> resultadoFormula) {
        this.resultadoFormula = resultadoFormula;
    }

    public Resultado<Double> getResultadoReferencia() {
        return resultadoReferencia;
    }

    public void setResultadoReferencia(Resultado<Double> resultadoReferencia) {
        this.resultadoReferencia = resultadoReferencia;
    }

    public Resultado<Boolean> getResultadoRegra() {
        return resultadoRegra;
    }

    public void setResultadoRegra(Resultado<Boolean> resultadoRegra) {
        this.resultadoRegra = resultadoRegra;
    }

    public Resultado<Double> getResultadoValorIntegral() {
        return resultadoValorIntegral;
    }

    public void setResultadoValorIntegral(Resultado<Double> resultadoValorIntegral) {
        this.resultadoValorIntegral = resultadoValorIntegral;
    }

    public Resultado<Double> getResultadoValorBaseDeCalculo() {
        return resultadoValorBaseDeCalculo;
    }

    public void setResultadoValorBaseDeCalculo(Resultado<Double> resultadoValorBaseDeCalculo) {
        this.resultadoValorBaseDeCalculo = resultadoValorBaseDeCalculo;
    }
}
