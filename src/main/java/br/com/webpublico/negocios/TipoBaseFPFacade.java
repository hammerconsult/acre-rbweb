/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoBaseFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoBaseFPFacade extends AbstractFacade<TipoBaseFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoBaseFPFacade() {
        super(TipoBaseFP.class);
    }

    public boolean isCodigoExistente(TipoBaseFP tipoBaseFP) {
        String hql = "from TipoBaseFP where codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", tipoBaseFP.getCodigo());

        List<TipoBaseFP> lista = q.getResultList();

        if(lista != null && !lista.isEmpty()) {
            return true;
        }

        return false;
    }
}
