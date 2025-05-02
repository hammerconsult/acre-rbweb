package br.com.webpublico.exception;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Tributo;


public class SemUnidadeException extends Exception {

    public SemUnidadeException(String string) {
        super(string);
    }
}
