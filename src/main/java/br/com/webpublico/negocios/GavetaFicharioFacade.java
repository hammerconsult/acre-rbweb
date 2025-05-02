/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Fichario;
import br.com.webpublico.entidades.GavetaFichario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author andre
 */
@Stateless
public class GavetaFicharioFacade extends AbstractFacade<GavetaFichario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GavetaFicharioFacade() {
        super(GavetaFichario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<GavetaFichario> listaGavetasPorFichario(Fichario fichario) {
        Query q = em.createQuery(" select gaveta from GavetaFichario gaveta "
                + " where gaveta.fichario = :fichario "
                + " order by gaveta.codigo ");
        q.setParameter("fichario", fichario);
        return q.getResultList();
    }

    public List<GavetaFichario> completaGavetasPorFichario(String s, Fichario fichario) {
        Query q = em.createQuery(" select gaveta from GavetaFichario gaveta "
                + " where gaveta.fichario = :fichario "
                + " and (lower(cast(codigo as string)) like :filtro "
                + " or lower(descricao) like :filtro)"
                + " order by gaveta.codigo ");
        q.setParameter("fichario", fichario);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
