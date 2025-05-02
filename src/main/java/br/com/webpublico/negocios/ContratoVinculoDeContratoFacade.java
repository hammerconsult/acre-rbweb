/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ContratoVinculoDeContrato;
import br.com.webpublico.util.UtilRH;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class ContratoVinculoDeContratoFacade extends AbstractFacade<ContratoVinculoDeContrato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ContratoVinculoDeContratoFacade() {
        super(ContratoVinculoDeContrato.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ContratoVinculoDeContrato> recuperaContratoVinculoDeContrato(ContratoFP contratoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from ContratoVinculoDeContrato contratovinc");
        hql.append(" where contratovinc.contratoFP = :contrato");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        return q.getResultList();
    }

    public ContratoVinculoDeContrato recuperaContratoVinculoDeContratoVigente(ContratoFP contratoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append("select contratovinc from ContratoVinculoDeContrato contratovinc");
        hql.append(" where contratovinc.contratoFP = :contrato order by contratovinc.inicioVigencia desc,contratovinc.finalVigencia desc");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(1);
        q.setParameter("contrato", contratoFP);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ContratoVinculoDeContrato) q.getResultList().get(0);
    }

    public ContratoVinculoDeContrato recuperaContratoVinculoDeContratoVigente(ContratoFP contratoFP, Date dataVigencia) {
        StringBuilder hql = new StringBuilder();
        hql.append("select contratovinc from ContratoVinculoDeContrato contratovinc ");
        hql.append(" where contratovinc.contratoFP = :contrato ");
        hql.append(" and :dataVigencia between contratovinc.inicioVigencia and coalesce(contratovinc.finalVigencia, :dataVigencia) ");
        hql.append(" order by contratovinc.inicioVigencia desc, contratovinc.finalVigencia desc");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(1);
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataVigencia", dataVigencia);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ContratoVinculoDeContrato) q.getResultList().get(0);
    }

}
