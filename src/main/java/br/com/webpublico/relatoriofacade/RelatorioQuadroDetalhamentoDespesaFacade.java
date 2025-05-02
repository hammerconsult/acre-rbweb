package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.FuncaoFacade;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.negocios.SubFuncaoFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 24/03/14
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@Deprecated //Está no Webreport
public class RelatorioQuadroDetalhamentoDespesaFacade extends RelatorioContabilSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProjetoAtividadeFacade acaoFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;

    public ProjetoAtividadeFacade getAcaoFacade() {
        return acaoFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }
}
