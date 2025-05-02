/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.nfse.exceptions;

import java.util.Arrays;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
public class NfseValidacaoException extends NfseOperacaoNaoPermitidaException {

    public NfseValidacaoException() {
    }

    public NfseValidacaoException(String message) {
        super(message);
    }

    public NfseValidacaoException(String message, Throwable e) {
        super(message, e);
    }

    public NfseValidacaoException(String message, List<String> mensagens) {
        super(message, mensagens);
    }


    public NfseValidacaoException(String... mensagensParametro) {
        super("");
        adicionarMensagem(Arrays.asList(mensagensParametro));
    }
}
