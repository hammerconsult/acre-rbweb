package br.com.webpublico.interfaces;

import br.com.webpublico.exception.ValidacaoException;

/**
 * @author Felipe Marinzeck
 */
public interface ValidadorEntidade {
    public void validarConfirmacao() throws ValidacaoException;
}
