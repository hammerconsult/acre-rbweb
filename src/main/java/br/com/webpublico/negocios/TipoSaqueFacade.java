/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAdmissaoFGTS;
import br.com.webpublico.entidades.TipoSaque;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoSaqueFacade extends AbstractFacade<TipoSaque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoSaqueFacade() {
        super(TipoSaque.class);
    }

    public boolean existeCodigo(TipoSaque tipoSaque) {
        String hql = " from TipoSaque tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoSaque.getCodigo());

        List<TipoAdmissaoFGTS> lista = new ArrayList<TipoAdmissaoFGTS>();
        lista = q.getResultList();

        if (lista.contains(tipoSaque)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public TipoSaque recuperaTipoSaquePorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from TipoSaque tipo where tipo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (TipoSaque) q.getSingleResult();
    }
}
