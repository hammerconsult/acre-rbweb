/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script.rh;

import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.interfaces.GeraFuncao;

import java.io.Serializable;

/**
 * @author gecen
 */
public class GeraFuncaoValorIntegral implements GeraFuncao, Serializable {

    @Override
    public String executarGeracao(String nomeFunction, String corpo, String codigoEventoFP, TipoExecucaoEP tipoExecucaoEP) {
        return "function " + geraAssinaturaParaChamada(nomeFunction) + "{\n"
                + corpo + "\n"
                + "};\n"
                + "\n";
    }

    @Override
    public String geraAssinaturaParaChamada(String nomeFunction) {
        return nomeFunction + "(" + PARAMETRO_ENTIDADE_PAGAVEL + ")";
    }
}
