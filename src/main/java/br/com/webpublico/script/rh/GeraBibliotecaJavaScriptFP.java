/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.FormulaPadraoFP;
import br.com.webpublico.enums.TipoExecucaoEP;

import java.io.Serializable;
import java.util.List;

/**
 * Gera a string com as funções javascript
 *
 * @author gecen
 */
public class GeraBibliotecaJavaScriptFP implements Serializable{

    private GeraFuncaoRegra functionRegra = new GeraFuncaoRegra();
    private GeraFuncaoFormula functionFormula = new GeraFuncaoFormula();
    private GeraFuncaoValorIntegral functionValorIntegral = new GeraFuncaoValorIntegral();
    private GeraFuncaoReferencia functionReferencia = new GeraFuncaoReferencia();
    private GeraFuncaoValorBaseDeCalculo functionValorBaseDeCalculo = new GeraFuncaoValorBaseDeCalculo();
    GeraFuncaoBaseCalculo baseCalculo = new GeraFuncaoBaseCalculo();

    /**
     * Gera a string com as funções javascript. <br>
     * O seguinte padrão de nomes é utilizado para gerar as funções:<br>
     * <p/>
     * <p/>
     * Para a regra:<br>
     * <p/>
     * <code>
     * function regraEventoFP_codigo(){
     * //conteúdo da variável regra
     * }
     * <code>
     * A regra deve retornar um boolean<br>
     * <p/>
     * Para a fórmula:<br>
     * <code>
     * function formulaEventoFP_codigo(){
     * //conteúdo da variável formula
     * }
     * <code>
     * A fórmula deve retornar um double.
     *
     * @param listEventoFPs Lista de EventoFP
     * @return String contendo o javascript gerado
     */
    public String gerar(List<EventoFP> listEventoFPs, List<FormulaPadraoFP> listFormulaPadraoFPs, List<BaseFP> listBaseFP, TipoExecucaoEP tipoExecucaoEP) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" var mapFormula = {};");
        for (EventoFP eventoFP : listEventoFPs) {
            stringBuilder.append(functionRegra.executarGeracao(eventoFP.nomeFuncaoRegra(), eventoFP.getRegra(), eventoFP.getCodigo(), tipoExecucaoEP));
            stringBuilder.append(functionFormula.executarGeracao(eventoFP.nomeFuncaoFormula(), eventoFP.getFormula(), eventoFP.getCodigo(), tipoExecucaoEP));
            stringBuilder.append(functionValorIntegral.executarGeracao(eventoFP.nomeValorIntegral(), eventoFP.getFormulaValorIntegral(), eventoFP.getCodigo(), tipoExecucaoEP));
            stringBuilder.append(functionReferencia.executarGeracao(eventoFP.nomeReferencia(), eventoFP.getReferencia(), eventoFP.getCodigo(), tipoExecucaoEP));
            stringBuilder.append(functionValorBaseDeCalculo.executarGeracao(eventoFP.nomeValorBaseDeCalculo(), eventoFP.getValorBaseDeCalculo(), eventoFP.getCodigo(), tipoExecucaoEP));
        }
        for (FormulaPadraoFP formulaPadraoFP : listFormulaPadraoFPs) {
            stringBuilder.append(formulaPadraoFP.getCodigoFonteFuncao());
        }
        for (BaseFP baseFP : listBaseFP) {
            stringBuilder.append(baseCalculo.geraBase(baseFP));
        }
        return stringBuilder.toString();
    }
}
