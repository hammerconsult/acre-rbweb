/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peixe
 */
public class FuncaoRegraTest {

    @Test
    public void testGerarFolha() {
        EventoFP efp1 = new EventoFP();
        efp1.setCodigo("10");
        efp1.setRegra("//CORPO REGRA 10");
        efp1.setFormula("//CORPO FORMULA 10");
        efp1.setFormulaValorIntegral("//CORPO VALOR INTEGRAL 10");
        efp1.setReferencia("//CORPO REFERENCIA 10");

        GeraFuncaoRegra functionRegra = new GeraFuncaoRegra();

        String expResult = "function regraEventoFP_10(ep){\n"
                + "//CORPO REGRA 10\n"
                + "};\n"
                + "\n";

        String result = functionRegra.executarGeracao(efp1.nomeFuncaoRegra(), efp1.getRegra(), efp1.getCodigo(), TipoExecucaoEP.FOLHA);
        //System.out.println("Resultado Esperado: " + expResult);
        //System.out.println("Resultado: " + result);
        assertEquals(expResult, result);

    }

    @Test
    public void testGerarRPA() {
        EventoFP efp1 = new EventoFP();
        efp1.setCodigo("10");
        efp1.setRegra("//CORPO REGRA 10");
        efp1.setFormula("//CORPO FORMULA 10");
        efp1.setFormulaValorIntegral("//CORPO VALOR INTEGRAL 10");
        efp1.setReferencia("//CORPO REFERENCIA 10");

        GeraFuncaoRegra functionRegra = new GeraFuncaoRegra();

        String expResult = "function regraEventoFP_10(ep){\n"
                + "//CORPO REGRA 10\n"
                + "};\n"
                + "\n";

        String result = functionRegra.executarGeracao(efp1.nomeFuncaoRegra(), efp1.getRegra(), efp1.getCodigo(), TipoExecucaoEP.RPA);

        //assertEquals(expResult, result);

    }
}
