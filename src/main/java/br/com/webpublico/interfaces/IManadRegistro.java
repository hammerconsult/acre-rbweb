package br.com.webpublico.interfaces;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.manad.ManadContabilFacade;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 13/06/14
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public interface IManadRegistro {

    public ManadRegistro.ManadModulo getModulo();

    public ManadRegistro.ManadRegistroTipo getTipoRegistro();

    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo);


}
