package br.com.webpublico.exception;

import br.com.webpublico.entidades.CalculoISS;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 02/08/13
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class EfetivacaoIssFixoGeralException extends Exception {
    //não existe a necessidade de encapsular
    public CalculoISS calculoISS;

    public EfetivacaoIssFixoGeralException(String msg) {
        super(msg);
    }

    public EfetivacaoIssFixoGeralException(String msg, CalculoISS calculo) {
        super(msg);
        this.calculoISS = calculo;
    }
}
