/*
 * Codigo gerado automaticamente em Mon Feb 21 15:06:01 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Pais;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class PaisFacade extends AbstractFacade<Pais> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PaisFacade() {
        super(Pais.class);
    }

    public Pais buscarPorCodigo(String codigo) {
        String hql = "from Pais p where p.codigo = :codigo or p.codigoBacen = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", Integer.valueOf(codigo));
        if (!q.getResultList().isEmpty()) {
            return (Pais) q.getResultList().get(0);
        }
        return null;
    }
}
