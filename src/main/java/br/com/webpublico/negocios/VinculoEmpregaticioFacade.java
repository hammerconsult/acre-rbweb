/*
 * Codigo gerado automaticamente em Tue Feb 07 11:11:42 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.VinculoEmpregaticio;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class VinculoEmpregaticioFacade extends AbstractFacade<VinculoEmpregaticio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VinculoEmpregaticioFacade() {
        super(VinculoEmpregaticio.class);
    }

    public boolean existeCodigo(VinculoEmpregaticio vinculoEmpregaticio) {
        String hql = " from VinculoEmpregaticio tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", vinculoEmpregaticio.getCodigo());
        if (((List<VinculoEmpregaticio>) q.getResultList()).contains(vinculoEmpregaticio)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
