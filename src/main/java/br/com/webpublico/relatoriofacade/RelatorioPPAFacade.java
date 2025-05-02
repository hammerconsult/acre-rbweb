package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.negocios.MacroObjetivoEstrategicoFacade;
import br.com.webpublico.negocios.PPAFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Created by mateus on 08/11/17.
 */
@Stateless
public class RelatorioPPAFacade  extends RelatorioContabilSuperFacade {

    @EJB
    private PPAFacade pPAFacade;
    @EJB
    private LDOFacade lDOFacade;
    @EJB
    private MacroObjetivoEstrategicoFacade macroObjetivoEstrategicoFacade;



    public PPAFacade getpPAFacade() {
        return pPAFacade;
    }

    public LDOFacade getlDOFacade() {
        return lDOFacade;
    }

    public MacroObjetivoEstrategicoFacade getMacroObjetivoEstrategicoFacade() {
        return macroObjetivoEstrategicoFacade;
    }
}
