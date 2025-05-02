/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Isencao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Heinz
 */
@Stateless
public class IsencaoFacede extends AbstractFacade<Isencao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IsencaoFacede() {
        super(Isencao.class);
    }

    @Override
    public void salvarNovo(Isencao entity) {
        super.salvarNovo(entity);
    }

    public boolean existeCodigoCategoria(Long codigo) {
        String sql = "SELECT * FROM isencao WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

}
