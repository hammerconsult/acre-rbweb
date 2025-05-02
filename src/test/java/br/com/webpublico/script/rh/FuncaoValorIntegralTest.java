/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import org.junit.Test;

/**
 *
 * @author peixe
 */
public class FuncaoValorIntegralTest {

    @Test
    public void testGerarFolha() {
        EventoFP efp1 = new EventoFP();
        efp1.setCodigo("10");
        efp1.setRegra("//CORPO REGRA 10");
        efp1.setFormula("//CORPO FORMULA 10");
        efp1.setFormulaValorIntegral("//CORPO VALOR INTEGRAL 10");
        efp1.setReferencia("//CORPO REFERENCIA 10");

        GeraFuncaoValorIntegral functionValorIntegral = new GeraFuncaoValorIntegral();

        String expResult = "function valorIntegralEventoFP_10(ep){\n"
                + "//CORPO VALOR INTEGRAL 10\n"
                + "};\n"
                + "\n";

        String result = functionValorIntegral.executarGeracao(efp1.nomeValorIntegral(), efp1.getFormulaValorIntegral(), efp1.getCodigo(), TipoExecucaoEP.FOLHA);
        junit.framework.Assert.assertEquals(expResult, result);

    }

    @Test
    public void testGerarRPA() {
        EventoFP efp1 = new EventoFP();
        efp1.setCodigo("10");
        efp1.setRegra("//CORPO REGRA 10");
        efp1.setFormula("//CORPO FORMULA 10");
        efp1.setFormulaValorIntegral("//CORPO VALOR INTEGRAL 10");
        efp1.setReferencia("//CORPO REFERENCIA 10");

        GeraFuncaoValorIntegral functionValorIntegral = new GeraFuncaoValorIntegral();

        String expResult = "function valorIntegralEventoFP_10(ep){\n"
                + "//CORPO VALOR INTEGRAL 10\n"
                + "};\n"
                + "\n";

        String result = functionValorIntegral.executarGeracao(efp1.nomeValorIntegral(), efp1.getFormulaValorIntegral(), efp1.getCodigo(), TipoExecucaoEP.RPA);
        junit.framework.Assert.assertEquals(expResult, result);

    }
}
