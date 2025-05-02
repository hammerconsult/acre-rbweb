/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.script.Resultado;

/**
 * @author gecen
 * @modificado peixe
 */
public interface ExecutaScriptEventoFP {

    Resultado<Double> executaFormula(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH);

    Resultado<Boolean> executaRegra(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH);

    Resultado<Double> executaReferencia(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH);

    Resultado<Double> executaValorIntegral(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH);

    Resultado<Double> executaValorBaseDeCalculo(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH);
}
