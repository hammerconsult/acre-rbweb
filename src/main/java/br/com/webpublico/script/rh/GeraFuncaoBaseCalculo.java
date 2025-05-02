/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.interfaces.GeraFuncao;
import br.com.webpublico.interfaces.NomeObjetosConstantesScriptFP;
import br.com.webpublico.interfaces.PrefixoNomeFormulasConstantesScriptFP;

import java.io.Serializable;

/**
 * @author peixe
 */
public class GeraFuncaoBaseCalculo implements Serializable {

    private GeraFuncaoRegra funcaoRegra = new GeraFuncaoRegra();
    private GeraFuncaoFormula funcaoFormula = new GeraFuncaoFormula();

    public String geraBase(BaseFP baseFP) {
        StringBuilder builder = new StringBuilder();
        builder.append("function " + PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_CALCULABASE).append(baseFP.getCodigo()).append("(" + GeraFuncao.PARAMETRO_ENTIDADE_PAGAVEL + "){\n");
        builder.append("    var _base = 0;\n");
        for (ItemBaseFP item : baseFP.getItensBasesFPs()) {
            builder.append("    if (").append(funcaoRegra.geraAssinaturaParaChamada(item.getEventoFP().nomeFuncaoRegra())).append("){\n");
            if (item.getOperacaoFormula() != null && item.getOperacaoFormula().equals(OperacaoFormula.ADICAO)) {
                builder.append("        _base += parseFloat(").append(funcaoFormula.geraAssinaturaParaChamada(item.getEventoFP().nomeFuncaoFormula())).append(");\n");
                builder.append("    }\n");
            }
            if (item.getOperacaoFormula() != null && item.getOperacaoFormula().equals(OperacaoFormula.SUBTRACAO)) {
                builder.append("        _base -= parseFloat(").append(funcaoFormula.geraAssinaturaParaChamada(item.getEventoFP().nomeFuncaoFormula())).append(");\n");
                builder.append("    }\n");
            }

        }
        builder.append("    if(" + NomeObjetosConstantesScriptFP.MAP_CALCULO_RETROATIVO + " != null){\n");
        builder.append("    var acumulado = " + NomeObjetosConstantesScriptFP.MAP_CALCULO_RETROATIVO + ".get(" + GeraFuncao.PARAMETRO_ENTIDADE_PAGAVEL + ".getId());\n");
        builder.append("    if(acumulado != null){\n");
        builder.append("    _base += parseFloat(acumulado);\n");
        builder.append("    }\n");
        builder.append("    }\n");
        builder.append("    return _base;\n");
        builder.append("}");
        return builder.toString();
    }
}
