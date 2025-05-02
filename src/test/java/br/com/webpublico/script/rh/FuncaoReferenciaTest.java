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
public class FuncaoReferenciaTest {

    @Test
    public void testGerarFolha() {
        EventoFP efp1 = new EventoFP();
        efp1.setCodigo("10");
        efp1.setRegra("//CORPO REGRA 10");
        efp1.setFormula("//CORPO FORMULA 10");
        efp1.setFormulaValorIntegral("//CORPO VALOR INTEGRAL 10");
        efp1.setReferencia("//CORPO REFERENCIA 10");

        GeraFuncaoReferencia functionReferencia = new GeraFuncaoReferencia();

        String expResult = "function referenciaEventoFP_10(ep){\n"
                + "var _quant = calculador.recuperaQuantificacaoLancamentoTipoReferencia(ep, '10', mes, ano);\n"
                + "if ( _quant >= 0 ) {\n"
                + "return _quant;\n"
                + "} else {\n"
                + "//CORPO REFERENCIA 10\n"
                + "}\n"
                + "};\n"
                + "\n";

        String result = functionReferencia.executarGeracao(efp1.nomeReferencia(), efp1.getReferencia(), efp1.getCodigo(), TipoExecucaoEP.FOLHA);
        //System.out.println("Resultado Esperado: " + expResult);
        //System.out.println("Resultado: " + result);
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
        GeraFuncaoReferencia functionReferencia = new GeraFuncaoReferencia();
        String expResult = "function referenciaEventoFP_10(ep){\n"
                + "//CORPO REFERENCIA 10\n"
                + "};\n"
                + "\n";
        String result = functionReferencia.executarGeracao(efp1.nomeReferencia(), efp1.getReferencia(), efp1.getCodigo(), TipoExecucaoEP.RPA);
        junit.framework.Assert.assertEquals(expResult, result);

    }
}
