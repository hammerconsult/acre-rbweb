package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.negocios.CategoriaDividaPublicaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConfigConciliacaoContabilCategoriaDividaPublicaFacade extends AbstractConfigConciliacaoContabilFacade {

    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;

    public CategoriaDividaPublicaFacade getCategoriaDividaPublicaFacade() {
        return categoriaDividaPublicaFacade;
    }
}
