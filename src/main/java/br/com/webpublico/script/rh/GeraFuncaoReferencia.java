/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.interfaces.GeraFuncao;
import br.com.webpublico.interfaces.NomeObjetosConstantesScriptFP;

import java.io.Serializable;

/**
 * @author peixe
 */
public class GeraFuncaoReferencia implements GeraFuncao, Serializable {

    @Override
    public String geraAssinaturaParaChamada(String nomeFunction) {
        return nomeFunction + "(" + PARAMETRO_ENTIDADE_PAGAVEL + ")";
    }

    @Override
    public String executarGeracao(String nomeFunction, String corpo, String codigoEventoFP, TipoExecucaoEP tipoExecucaoEP) {
        StringBuilder builder = new StringBuilder();
        builder.append("function ").append(geraAssinaturaParaChamada(nomeFunction)).append("{\n");
        if (tipoExecucaoEP.equals(TipoExecucaoEP.RPA)) {
            builder.append(corpo).append("\n");
        } else {
            builder.append("var _quant = " + NomeObjetosConstantesScriptFP.NOME_OBJETO_JAVA + ".recuperaQuantificacaoLancamentoTipoReferencia(ep, '").append(codigoEventoFP).append("', " + NomeObjetosConstantesScriptFP.NOME_OBJETO_MES_CALCULO + ", " + NomeObjetosConstantesScriptFP.NOME_OBJETO_ANO_CALCULO + ");\n");
            builder.append("if ( _quant >= 0 ) {\n");
            builder.append("return _quant;\n");
            builder.append("} else {\n");
            builder.append(corpo).append("\n");
            builder.append("}\n");
        }
        builder.append("};\n");
        builder.append("\n");
        return builder.toString();
    }
}
