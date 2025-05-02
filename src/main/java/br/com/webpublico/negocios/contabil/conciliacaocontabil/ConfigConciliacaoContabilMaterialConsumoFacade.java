package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.negocios.GrupoMaterialFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ConfigConciliacaoContabilMaterialConsumoFacade extends AbstractConfigConciliacaoContabilFacade {

    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }
}
