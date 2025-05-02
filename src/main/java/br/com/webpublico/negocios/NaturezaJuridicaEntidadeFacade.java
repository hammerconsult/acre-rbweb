/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.NaturezaJuridicaEntidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Leonardo
 */
@Stateless
public class NaturezaJuridicaEntidadeFacade extends AbstractFacade<NaturezaJuridicaEntidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public NaturezaJuridicaEntidade recuperar(Object id) {
        NaturezaJuridicaEntidade naturezaJuridicaEntidade = super.recuperar(id);
        forcarRegistroInicialNaAuditoria(naturezaJuridicaEntidade);
        return naturezaJuridicaEntidade;
    }

    public NaturezaJuridicaEntidadeFacade() {
        super(NaturezaJuridicaEntidade.class);
    }

    public boolean validaCodigo(NaturezaJuridicaEntidade naturezaJuridicaEntidade) {
        String sql = "SELECT id FROM NaturezaJuridicaEntidade WHERE codigo = :codigo";
        if (naturezaJuridicaEntidade.getId() != null) {
            sql += " AND ID != :ID";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", naturezaJuridicaEntidade.getCodigo());
        if (naturezaJuridicaEntidade.getId() != null) {
            q.setParameter("id", naturezaJuridicaEntidade.getId());
        }
        return !q.getResultList().isEmpty();
    }
}
