/*
 * Codigo gerado automaticamente em Fri Apr 08 10:50:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.IndiceEconomico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class IndiceEconomicoFacade extends AbstractFacade<IndiceEconomico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IndiceEconomicoFacade() {
        super(IndiceEconomico.class);
    }

    public IndiceEconomico recuperaPorDescricao(String descricao) {
        Query q = em.createQuery("from IndiceEconomico ie where trim(lower(ie.descricao)) = :descricao").setParameter("descricao", descricao.trim().toLowerCase());
        List<IndiceEconomico> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        } else {
            return null;
        }

    }
}
