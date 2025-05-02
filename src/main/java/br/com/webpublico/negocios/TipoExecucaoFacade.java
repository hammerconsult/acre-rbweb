/*
 * Codigo gerado automaticamente em Thu Apr 05 14:04:23 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoExecucao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TipoExecucaoFacade extends AbstractFacade<TipoExecucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ClausulaBenificiarioFacade clausulaBenificiarioFacade;
    @EJB
    private TipoDoctoHabilitacaoFacade tipoDoctoHabilitacaoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoExecucaoFacade() {
        super(TipoExecucao.class);
    }

    public ClausulaBenificiarioFacade getClausulaBenificiarioFacade() {
        return clausulaBenificiarioFacade;
    }

    public TipoDoctoHabilitacaoFacade getTipoDoctoHabilitacaoFacade() {
        return tipoDoctoHabilitacaoFacade;
    }

    @Override
    public TipoExecucao recuperar(Object id) {
        TipoExecucao te = em.find(TipoExecucao.class, id);
        te.getTipoExecucaoClausulaBens().size();
        te.getTipoExecucaoTipoCertidao().size();
        return te;
    }

    @Override
    public TipoExecucao recarregar(TipoExecucao entity) {
        TipoExecucao toReturn;
        String hql = "from TipoExecucao t where t=:param ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", entity);
        toReturn = (TipoExecucao) q.getSingleResult();
        toReturn.getTipoExecucaoClausulaBens().size();
        toReturn.getTipoExecucaoTipoCertidao().size();
        return toReturn;
    }
}
