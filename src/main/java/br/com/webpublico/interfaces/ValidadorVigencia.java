package br.com.webpublico.interfaces;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 27/03/15
 * Time: 13:01
 * To change this template use File | Settings | File Templates.
 */
public interface ValidadorVigencia {

    public Date getInicioVigencia();

    public Date getFimVigencia();

    public void setInicioVigencia(Date data);

    public void setFimVigencia(Date data);

}
