package br.com.webpublico.exception;

import br.com.webpublico.repositorios.jdbc.WebPublicoJDBCException;

/**
 * @author Daniel
 * @since 15/09/2015 09:12
 */
public class CampoVazioException extends WebPublicoJDBCException {

    public CampoVazioException(String nomeCampo) {
        super(nomeCampo + " não pode ser Null");
    }
}
