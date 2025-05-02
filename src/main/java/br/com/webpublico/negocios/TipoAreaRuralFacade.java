/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAreaRural;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Heinz
 */
@Stateless
public class TipoAreaRuralFacade extends AbstractFacade<TipoAreaRural> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAreaRuralFacade() {
        super(TipoAreaRural.class);
    }

    public boolean existeCodigoCategoria(Long codigo) {
        String sql = "SELECT * FROM tipoarearural WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }
}
