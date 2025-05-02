/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import br.com.webpublico.enums.TipoExecucaoEP;

/**
 * @author gecen
 */
public interface GeraFuncao {

    String PARAMETRO_ENTIDADE_PAGAVEL = "ep";

    String geraAssinaturaParaChamada(String nomeFunction);

    String executarGeracao(String nomeFunction, String corpo, String codigoEventoFP, TipoExecucaoEP tipoExecucaoEP);
}
