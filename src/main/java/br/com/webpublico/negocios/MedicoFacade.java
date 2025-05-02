/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Medico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MedicoFacade extends AbstractFacade<Medico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MedicoFacade() {
        super(Medico.class);
    }

    @Override
    public Medico recuperar(Object id) {
        Medico a = em.find(Medico.class, id);
        return a;
    }

    public List<Medico> listaFiltrandoMedico(String s) {
        String hql = " select am from Medico am "
                + " inner join am.medico pessoa "
                + " where am.medico.id = pessoa.id "
                + " and (lower(pessoa.nome) like :parametro "
                + " or lower(am.registroCRM) like :parametro ) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }
}
