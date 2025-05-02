/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.enums.TiposErroScript;
import br.com.webpublico.script.Resultado;
import br.com.webpublico.script.ExecutaScriptImpl;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.entidades.EventoFP;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gecen
 */
public class ExecutaScriptImplTest {

    @Test
    public void testInvocar() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String stringJavaScript = "function regraEventoFP_10(ep){\n"
                + "return true;\n"
                + "};\n";

        engine.eval(stringJavaScript);
        Invocable jsInvoke = (Invocable) engine;
//        Object objectLib = engine.get(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_SCRIPT);

        EventoFP eventoFP = new EventoFP();
        eventoFP.setCodigo("10");

        ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);

        Resultado<Boolean> resultadoRegra = executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class);
        assertTrue(resultadoRegra.naoTemErro());
        assertEquals(resultadoRegra.getValor(), Boolean.TRUE);
    }

    @Test
    public void testExecutaRegraErroGenericoJavaScript() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String stringJavaScript = "function regraEventoFP_10(ep){\n"
                + "test()\n"
                + "};\n"
                + "\n";

        engine.eval(stringJavaScript);

        Invocable jsInvoke = (Invocable) engine;
//        Object objectLib = engine.get(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_SCRIPT);

        EventoFP eventoFP = new EventoFP();
        eventoFP.setCodigo("10");

        ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);

        Resultado<Boolean> resultadoRegra = executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class);
        assertTrue(resultadoRegra.temErro());
        assertEquals(TiposErroScript.ERRO_GENERICO_EXECUCAO_SCRIPT, resultadoRegra.getItemErroScript().getTiposErroScript());

    }

    @Test
    public void testExecutaRegraFuncaoNaoEncontrada() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String stringJavaScript = "function regraEventoFP_XX(ep){\n"
                + "return false\n"
                + "};\n"
                + "\n";

        engine.eval(stringJavaScript);

        Invocable jsInvoke = (Invocable) engine;
//        Object objectLib = engine.get(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_SCRIPT);

        EventoFP eventoFP = new EventoFP();
        eventoFP.setCodigo("10");

        ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);

        Resultado<Boolean> resultadoRegra = executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class);

        assertTrue(resultadoRegra.temErro());
        assertEquals(TiposErroScript.ERRO_FUNCAO_NAO_ENCONTRADA, resultadoRegra.getItemErroScript().getTiposErroScript());

    }

    @Test
    public void testExecutaRegraRetornoInvalido() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String stringJavaScript = "function regraEventoFP_10(ep){\n"
                + "return 'x'\n"
                + "};\n"
                + "\n";

        engine.eval(stringJavaScript);

        Invocable jsInvoke = (Invocable) engine;
//        Object objectLib = engine.get(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_SCRIPT);

        EventoFP eventoFP = new EventoFP();
        eventoFP.setCodigo("10");

        ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);

        Resultado<Boolean> resultadoRegra = executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class);

        assertTrue(resultadoRegra.temErro());
        assertEquals(TiposErroScript.ERRO_TIPO_RETORNO_INCOMPATIVEL, resultadoRegra.getItemErroScript().getTiposErroScript());

    }

    @Test
    public void testExecutaRegraErroDesconhecido() throws ScriptException {

        ScriptEngineManager manager = new ScriptEngineManager();

        ScriptEngine engine = manager.getEngineByName("JavaScript");

        String stringJavaScript = "function regraEventoFP_10(ep){\n"
                + "return true\n"
                + "};\n"
                + "\n";

        engine.eval(stringJavaScript);

        //passando null para executar o script
        //Invocable jsInvoke = (Invocable) engine;
//        Object objectLib = engine.get(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA_SCRIPT);

        EventoFP eventoFP = new EventoFP();
        eventoFP.setCodigo("10");

        ExecutaScript executaScript = new ExecutaScriptImpl(null);
        Resultado<Boolean> resultadoRegra = executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class);

        assertTrue(resultadoRegra.temErro());
        assertEquals(TiposErroScript.ERRO_DESCONHECIDO, resultadoRegra.getItemErroScript().getTiposErroScript());

    }
}
