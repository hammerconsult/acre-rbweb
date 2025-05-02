package br.com.webpublico.interfaces;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 27/03/15
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
public interface ValidadorVigenciaFolha extends ValidadorVigencia{
    public BigDecimal getValor();
}
