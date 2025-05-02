package br.com.webpublico.interfaces;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 19/09/13
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
public interface AssitenteDoGeradorDeIds {
    public int getQuantidade();

    public JdbcDaoSupport getDao();
}
