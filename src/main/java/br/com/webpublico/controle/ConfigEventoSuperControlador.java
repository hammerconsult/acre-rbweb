package br.com.webpublico.controle;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.negocios.UtilConfiguracaoEventoContabilFacade;

import javax.ejb.EJB;

/**
 * Created by mateus on 09/03/18.
 */
public abstract class ConfigEventoSuperControlador<T> extends PrettyControlador<T> {

    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigEventoSuperControlador(Class classe) {
        super(classe);
    }

    public boolean isVigenciaEncerrada() {
        return ((ValidadorVigencia) selecionado).getFimVigencia() != null &&  utilFacade.isVigenciaEncerrada(((ValidadorVigencia) selecionado).getFimVigencia());
    }
}
