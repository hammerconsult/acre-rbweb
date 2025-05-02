/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.FormulaPadraoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.interfaces.ExecutaScriptEventoFP;
import br.com.webpublico.interfaces.NomeObjetosConstantesScriptFP;
import br.com.webpublico.negocios.FuncoesFolhaFacade;
import br.com.webpublico.script.ExecutaScriptImpl;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author gecen
 */
public class ConfiguracaoContextoJS implements Serializable {

    private GeraBibliotecaJavaScriptFP geraBibliotecaJavaScript;
    private List<EventoFP> listEventoFPs;
    private List<FormulaPadraoFP> listFormulaPadraoFPs;
    private List<BaseFP> listBaseFP;
    private FuncoesFolhaFacade funcoesFolhaFacade;
    private Integer mes;
    private Integer ano;
    private ExecutaScriptEventoFP executaScriptEventoFP;
    private ScriptEngine engine;
    private String libJavaScript;

    public ConfiguracaoContextoJS(
            GeraBibliotecaJavaScriptFP geraBibliotecaJavaScript,
            List<EventoFP> listEventoFPs,
            List<FormulaPadraoFP> listFormulaPadraoFPs,
            List<BaseFP> listBaseFP,
            FuncoesFolhaFacade funcoesFolhaFacade,
            Integer mes,
            Integer ano) throws ScriptException {
        this.geraBibliotecaJavaScript = geraBibliotecaJavaScript;
        this.listEventoFPs = listEventoFPs;
        this.listFormulaPadraoFPs = listFormulaPadraoFPs;
        this.listBaseFP = listBaseFP;
        this.funcoesFolhaFacade = funcoesFolhaFacade;
        this.mes = mes;
        this.ano = ano;
        this.executaScriptEventoFP = buildContext();
    }

    private ExecutaScriptEventoFP buildContext() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        libJavaScript = geraBibliotecaJavaScript.gerar(listEventoFPs, listFormulaPadraoFPs, listBaseFP, TipoExecucaoEP.FOLHA);
        engine = manager.getEngineByName("JavaScript");
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA, funcoesFolhaFacade);
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_MES_CALCULO, mes);
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_ANO_CALCULO, ano);
        engine.eval(libJavaScript);
        Invocable jsInvoke = (Invocable) engine;
        ExecutaScript executaScript = new ExecutaScriptImpl(jsInvoke);
        ExecutaScriptEventoFPImpl instance = new ExecutaScriptEventoFPImpl(executaScript);
        putMap(null);
        return instance;
    }

    public String getLibJavaScript() {
        return libJavaScript;
    }

    public void updateAno(Integer ano) {
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_ANO_CALCULO, ano);
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public void updateMes(Integer mes) {
        engine.put(NomeObjetosConstantesScriptFP.NOME_OBJETO_MES_CALCULO, mes);
    }

    public void updateMesAno(Integer mes, Integer ano) {
        updateMes(mes);
        updateAno(ano);
    }

    public Integer getAno() {
        return ano;
    }

    public Integer getMes() {
        return mes;
    }

    public ExecutaScriptEventoFP getExecutaScriptEventoFP() {
        return executaScriptEventoFP;
    }

    public void putMap(Map<Long, BigDecimal> mapa) {
        engine.put(NomeObjetosConstantesScriptFP.MAP_CALCULO_RETROATIVO, mapa);
    }
}
