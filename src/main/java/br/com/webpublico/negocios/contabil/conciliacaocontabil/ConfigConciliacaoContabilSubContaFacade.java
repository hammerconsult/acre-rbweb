package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.negocios.SubContaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConfigConciliacaoContabilSubContaFacade extends AbstractConfigConciliacaoContabilFacade {

    @EJB
    private SubContaFacade subContaFacade;

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }
}
