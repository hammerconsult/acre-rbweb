package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.negocios.GrupoBemFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ConfigConciliacaoContabilGrupoBemMovelFacade extends AbstractConfigConciliacaoContabilFacade {

    @EJB
    private GrupoBemFacade grupoBemFacade;

    public List<GrupoBem> buscarGruposPatrimoniaisMoveis(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
    }
}
