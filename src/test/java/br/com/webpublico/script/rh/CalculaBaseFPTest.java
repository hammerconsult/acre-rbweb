/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.BaseFP;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.enums.OperacaoFormula;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gécen Dacome De Marchi
 */
public class CalculaBaseFPTest {

    /**
     * Test of gerar method, of class GeraBibliotecaJavaScriptFP.
     */
    @Test
    public void testCalculaBase() throws ScriptException, NoSuchMethodException {
        List<ItemBaseFP> itens = new ArrayList<ItemBaseFP>();
        BaseFP baseFP = new BaseFP();
        baseFP.setCodigo("1001");
        EventoFP efp1 = new EventoFP();

        efp1.setCodigo("10");
        ItemBaseFP ibfp1 = new ItemBaseFP();
        ibfp1.setEventoFP(efp1);
        ibfp1.setOperacaoFormula(OperacaoFormula.ADICAO);
        ibfp1.setBaseFP(baseFP);
        itens.add(ibfp1);

        EventoFP efp2 = new EventoFP();
        efp2.setCodigo("11");
        ItemBaseFP ibfp2 = new ItemBaseFP();
        ibfp2.setEventoFP(efp2);
        ibfp2.setOperacaoFormula(OperacaoFormula.SUBTRACAO);
        ibfp2.setBaseFP(baseFP);
        itens.add(ibfp2);

        baseFP.setItensBasesFPs(itens);


        String script = "function formulaEventoFP_10(ep){\n"
                + "return 100.00;\n"
                + "};\n"
                + "\n"
                + "function formulaEventoFP_11(ep){\n"
                + "return 10.00;\n"
                + "};\n"
                + "\n"
                + "function calculaBaseFP_1001(){\n"
                + "return calculaBaseFP_1001(ep);\n"
                + "};\n";
        //
        //                         + "folhaScript.calculaBase = function(itens, ep){\n"
        //                         + "   var evento; \n"
        //                         + "   var item; \n"
        //                         + "   var totalBase = 0; \n"
        //                         + "   var valorEvento; \n"
        //
        //                         + "   for (i = 0; i < itens.length; i++){ \n"
        //                         + "       item = itens[i]; \n"
        //                         + "       evento = item.getEventoFP(); \n"
        //                         + "       valorEvento = folhaScript[evento.nomeFuncaoFormula()](ep); \n"
        //                         + "       if (item.getOperacaoFormula().getSimbolo() == '+'){ \n"
        //                         + "           totalBase = totalBase + valorEvento; \n"
        //                         + "       } else { \n"
        //                         + "           totalBase = totalBase - valorEvento; \n"
        //                         + "       } \n"
        //                         + "   } \n"
        //                         + "   return totalBase; \n"



        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        engine.eval(script);
//        Object obj = engine.get("folhaScript");

//        ItemBaseFP[] itensArray = new ItemBaseFP[itens.size()];
//        engine.put("arrayItens", itens.toArray(itensArray));
        engine.put("ep", new Object());

        Invocable inv = (Invocable) engine;

        assertEquals(100.0, inv.invokeFunction("formulaEventoFP_10"));


        //System.out.println(inv.invokeFunction("formulaEventoFP_10"));


    }
    /*
     *
     */
}
