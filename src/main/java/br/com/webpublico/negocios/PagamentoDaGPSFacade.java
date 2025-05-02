/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PagamentoDaGPS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PagamentoDaGPSFacade extends AbstractFacade<PagamentoDaGPS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagamentoDaGPSFacade() {
        super(PagamentoDaGPS.class);
    }

    public boolean existeCodigo(PagamentoDaGPS pagamentoDaGPS) {
        String hql = " from PagamentoDaGPS p where p.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", pagamentoDaGPS.getCodigo());

        List<PagamentoDaGPS> lista = new ArrayList<PagamentoDaGPS>();
        lista = q.getResultList();

        if (lista.contains(pagamentoDaGPS)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<PagamentoDaGPS> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from PagamentoDaGPS p "
                + " where cast(p.codigo as string) like :parametro "
                + " or lower(p.descricao) like :parametro");
        q.setMaxResults(10);
        q.setParameter("parametro", "%" + s.toLowerCase().trim() + "%");
        return q.getResultList();
    }
}
