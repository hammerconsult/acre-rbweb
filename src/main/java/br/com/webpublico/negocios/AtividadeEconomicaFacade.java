/*
 * Codigo gerado automaticamente em Fri Feb 11 14:12:47 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtividadeEconomica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AtividadeEconomicaFacade extends AbstractFacade<AtividadeEconomica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtividadeEconomicaFacade() {
        super(AtividadeEconomica.class);
    }


    public List<AtividadeEconomica> listaFiltrando(String s) {
        String hql = "from AtividadeEconomica obj where ";
        hql += "lower(obj.descricao) like :filtro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }


}
