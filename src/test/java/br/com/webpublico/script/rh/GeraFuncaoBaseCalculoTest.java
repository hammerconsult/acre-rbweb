/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.enums.OperacaoFormula;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peixe
 */
public class GeraFuncaoBaseCalculoTest {

    @Test
    public void testGeraBase() {

        BaseFP baseFP = new BaseFP();
        baseFP.setCodigo("1001");


        EventoFP evento = new EventoFP();
        evento.setCodigo("10");
        ItemBaseFP itemBaseFP = new ItemBaseFP();
        itemBaseFP.setEventoFP(evento);
        itemBaseFP.setOperacaoFormula(OperacaoFormula.ADICAO);

        EventoFP evento2 = new EventoFP();
        evento2.setCodigo("20");
        ItemBaseFP itemBaseFP2 = new ItemBaseFP();
        itemBaseFP2.setEventoFP(evento2);
        itemBaseFP2.setOperacaoFormula(OperacaoFormula.SUBTRACAO);

        baseFP.setItensBasesFPs(new ArrayList<ItemBaseFP>());
        baseFP.getItensBasesFPs().add(itemBaseFP);
        baseFP.getItensBasesFPs().add(itemBaseFP2);


        GeraFuncaoBaseCalculo instance = new GeraFuncaoBaseCalculo();

        String expResult = "function calculaBaseFP_1001(ep){\n"
                + "    var _base = 0;\n"
                + "    if (regraEventoFP_10(ep)){\n"
                + "        _base += parseFloat(formulaEventoFP_10(ep));\n"
                + "    }\n"
                + "    if (regraEventoFP_20(ep)){\n"
                + "        _base -= parseFloat(formulaEventoFP_20(ep));\n"
                + "    }\n"
                + "    if(mapAcumuladoRetroativo != null){\n"
                + "    var acumulado = mapAcumuladoRetroativo.get(ep.getId());\n"
                + "    if(acumulado != null){\n"
                + "    _base += parseFloat(acumulado);\n"
                + "    }\n"
                + "    }\n"
                + "    return _base;\n"
                + "}";

        String result = instance.geraBase(baseFP);

        //System.out.println("-----------------Resultado----------------\n");
        //System.out.println("\n");
        //System.out.println(result);


        //System.out.println("-----------------Esperado----------------\n");
        //System.out.println(expResult);

        assertEquals(expResult, result);


    }
}
