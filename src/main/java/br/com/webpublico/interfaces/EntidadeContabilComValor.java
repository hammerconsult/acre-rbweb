package br.com.webpublico.interfaces;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 16/04/14
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public interface EntidadeContabilComValor extends EntidadeContabil {
    BigDecimal getValor();
}
