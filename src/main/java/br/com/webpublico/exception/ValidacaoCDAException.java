/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.exception;

import br.com.webpublico.entidades.CertidaoDividaAtiva;

import javax.ejb.ApplicationException;


@ApplicationException(rollback = true)
public class ValidacaoCDAException extends Exception {

    private CertidaoDividaAtiva.ValidacoesCDA validacao;

    public ValidacaoCDAException() {
        super();
    }

    public ValidacaoCDAException(CertidaoDividaAtiva.ValidacoesCDA validacao){
        super("CDA Inválida, de um getValidacao() para obeter as mensagens de validação");
        this.validacao = validacao;
    }

    public CertidaoDividaAtiva.ValidacoesCDA getValidacao() {
        return validacao;
    }
}
