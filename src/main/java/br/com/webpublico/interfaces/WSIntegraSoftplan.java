/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * WebService para integração com software de procuradoria PGM.net
 *
 * @author Arthur
 */
@WebService(name = "WSIntegraSoftplan", targetNamespace = "http://integra.ws.sistemasprefeitura.com.br/")
public interface WSIntegraSoftplan {

    @WebMethod
    String informacaoDecisaoJudicial(String xml);

    @WebMethod
    String informacaoAjuizamento(String xml);

    @WebMethod
    String informacaoPenhora(String xml);

    @WebMethod
    String recuperarSaldoAtualizado(String nuCDA);
}
