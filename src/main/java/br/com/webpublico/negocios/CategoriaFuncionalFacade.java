/*
 * Codigo gerado automaticamente em Thu Nov 10 16:24:46 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaFuncional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CategoriaFuncionalFacade extends AbstractFacade<CategoriaFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFuncionalFacade() {
        super(CategoriaFuncional.class);
    }

    public boolean existeCodigoDaCategoria(CategoriaFuncional categoria) {
        String hql = " from CategoriaFuncional c where c.codigo = :codigoParametro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", categoria.getCodigo());

        List<CategoriaFuncional> lista = q.getResultList();

        if (lista.contains(categoria)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
