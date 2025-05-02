/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.ExecutaScriptEventoFP;
import br.com.webpublico.script.Resultado;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;

import static org.easymock.EasyMock.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gecen
 */
public class ExecutaScriptEventoFPImplTest {

    @Test
    public void testExecutaRegra() {
        ExecutaScript executaScriptMock = createMock(ExecutaScript.class);

        Resultado<Boolean> resultado = new Resultado<Boolean>();

        EntidadePagavelRH vinculoFP = new VinculoFP();
        EventoFP eventoFP = new EventoFP();
        expect(executaScriptMock.invocar("regraEventoFP_10", Boolean.class, vinculoFP, eventoFP)).andReturn(resultado);
        replay(executaScriptMock);

        ExecutaScriptEventoFP executaScriptEventoFP = new ExecutaScriptEventoFPImpl(executaScriptMock);
        EventoFP efp = new EventoFP();
        efp.setCodigo("10");
        Resultado<Boolean> resultadoCall = executaScriptEventoFP.executaRegra(efp, vinculoFP);

        verify(executaScriptMock);
        assertEquals(resultado, resultadoCall);

    }

    @Test
    public void testExecutaFormula() {
        ExecutaScript executaScriptMock = createMock(ExecutaScript.class);

        Resultado<Double> resultado = new Resultado<Double>();

        EntidadePagavelRH vinculoFP = new VinculoFP();
        EventoFP eventoFP = new EventoFP();
        expect(executaScriptMock.invocar("formulaEventoFP_10", Double.class, vinculoFP, eventoFP)).andReturn(resultado);
        replay(executaScriptMock);

        ExecutaScriptEventoFP executaScriptEventoFP = new ExecutaScriptEventoFPImpl(executaScriptMock);
        EventoFP efp = new EventoFP();
        efp.setCodigo("10");
        Resultado<Double> resultadoCall = executaScriptEventoFP.executaFormula(efp, vinculoFP);

        verify(executaScriptMock);
        assertEquals(resultado, resultadoCall);

    }

    @Test
    public void testExecutaReferencia() {
        ExecutaScript executaScriptMock = createMock(ExecutaScript.class);

        Resultado<Double> resultado = new Resultado<Double>();

        EntidadePagavelRH vinculoFP = new VinculoFP();
        EventoFP eventoFP = new EventoFP();
        expect(executaScriptMock.invocar("referenciaEventoFP_10", Double.class, vinculoFP, eventoFP)).andReturn(resultado);
        replay(executaScriptMock);

        ExecutaScriptEventoFP executaScriptEventoFP = new ExecutaScriptEventoFPImpl(executaScriptMock);
        EventoFP efp = new EventoFP();
        efp.setCodigo("10");
        Resultado<Double> resultadoCall = executaScriptEventoFP.executaReferencia(efp, vinculoFP);

        verify(executaScriptMock);
        assertEquals(resultado, resultadoCall);

    }

    @Test
    public void testExecutaValorIntegral() {
        ExecutaScript executaScriptMock = createMock(ExecutaScript.class);

        Resultado<Double> resultado = new Resultado<Double>();

        EntidadePagavelRH vinculoFP = new VinculoFP();
        EventoFP eventoFP = new EventoFP();
        expect(executaScriptMock.invocar("valorIntegralEventoFP_10", Double.class, vinculoFP, eventoFP)).andReturn(resultado);
        replay(executaScriptMock);

        ExecutaScriptEventoFP executaScriptEventoFP = new ExecutaScriptEventoFPImpl(executaScriptMock);
        EventoFP efp = new EventoFP();
        efp.setCodigo("10");
        Resultado<Double> resultadoCall = executaScriptEventoFP.executaValorIntegral(efp, vinculoFP);

        verify(executaScriptMock);
        assertEquals(resultado, resultadoCall);

    }
}
