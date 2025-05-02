/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;


import br.com.webpublico.entidades.NumeroSerie;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class NumeroSerieFacade extends AbstractFacade<NumeroSerie> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NumeroSerieFacade() {
        super(NumeroSerie.class);
    }

    public boolean serieJaExite(NumeroSerie selecionado) {
        String hql = "from NumeroSerie n where n.serie = :serie ";
        if (selecionado.getId() != null) {
            hql += "and n != :numero";
        }
        Query q = em.createQuery(hql);
        if (selecionado.getId() != null) {
            q.setParameter("numero", selecionado);
        }
        q.setParameter("serie", selecionado.getSerie());
        return !q.getResultList().isEmpty();
    }

    public boolean descricaoJaExite(NumeroSerie selecionado) {
        String hql = "from NumeroSerie n where upper(replace(n.descricao, ' ', ''))  = :descricao ";
        if (selecionado.getId() != null) {
            hql += "and n != :numero";
        }
        Query q = em.createQuery(hql);
        if (selecionado.getId() != null) {
            q.setParameter("numero", selecionado);
        }
        q.setParameter("descricao", selecionado.getDescricao().trim().replaceAll(" ", "").toUpperCase());
        return !q.getResultList().isEmpty();
    }

}
