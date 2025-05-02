/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.interfaces.GeraFuncao;

import java.io.Serializable;

/**
 * @author andre
 */
public class GeraFuncaoValorBaseDeCalculo implements GeraFuncao , Serializable {

    @Override
    public String geraAssinaturaParaChamada(String nomeFunction) {
        return nomeFunction + "(" + PARAMETRO_ENTIDADE_PAGAVEL + ")";
    }

    @Override
    public String executarGeracao(String nomeFunction, String corpo, String codigoEventoFP, TipoExecucaoEP tipoExecucaoEP) {
        StringBuilder builder = new StringBuilder();
        builder.append("function ").append(geraAssinaturaParaChamada(nomeFunction)).append("{\n");
        builder.append(corpo).append("\n");
        builder.append("};\n");
        builder.append("\n");
        return builder.toString();
    }
}
