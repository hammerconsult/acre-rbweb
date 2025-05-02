/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcessoSubsidio;
import br.com.webpublico.entidades.CargoConfianca;
import br.com.webpublico.entidades.EnquadramentoCC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author claudio
 */
@Stateless
public class EnquadramentoCCFacade extends AbstractFacade<EnquadramentoCC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EnquadramentoCCFacade() {
        super(EnquadramentoCC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoCC getEnquadramentoCCPorCargoConfianca(CargoConfianca cargoConfianca) {
        String hql = "select enquadramento from EnquadramentoCC enquadramento "
            + "inner join enquadramento.categoriaPCS categoria "
            + "inner join enquadramento.progressaoPCS progressao "
            + "where enquadramento.cargoConfianca = :cargoConfianca "
            + "and enquadramento.finalVigencia is null";

        Query q = em.createQuery(hql);
        q.setParameter("cargoConfianca", cargoConfianca);

        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        } else {
            return (EnquadramentoCC) q.getResultList().get(0);
        }
    }

    public EnquadramentoCC getEnquadramentoCCPorAcessoSubsidio(AcessoSubsidio acessoSubsidio) {
        String hql = "select enquadramento from EnquadramentoCC enquadramento "
            + "inner join enquadramento.categoriaPCS categoria "
            + "inner join enquadramento.progressaoPCS progressao "
            + "where enquadramento.acessoSubsidio = :acessoSubsidio "
            + "and enquadramento.finalVigencia is null";

        Query q = em.createQuery(hql);
        q.setParameter("acessoSubsidio", acessoSubsidio);

        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return null;
        } else {
            return (EnquadramentoCC) q.getResultList().get(0);
        }
    }
}
