package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AssociacaoVinculoFP;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SindicatoVinculoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 23/07/13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AssociacaoVinculoFPFacade extends AbstractFacade<AssociacaoVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AssociacaoVinculoFPFacade() {
        super(AssociacaoVinculoFP.class);
    }

    public AssociacaoVinculoFP retornaUltimaAssociacao(ContratoFP contratoFP) {
        try {
            String hql = "select associacao from AssociacaoVinculoFP associacao " +
                    " where associacao.vinculoFP = :contratoFP " +
                    " and associacao.inicioVigencia = (select max(assoc.inicioVigencia) from AssociacaoVinculoFP assoc where assoc.vinculoFP = :contratoFP)";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            return (AssociacaoVinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
