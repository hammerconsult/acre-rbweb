package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.ObjetoCompra;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 18/09/14
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public interface CaracterizadorDeBemMovel extends CaracterizadorDeBem {

    ObjetoCompra getObjetoCompra();

    String getRegistroAnterior();
}
