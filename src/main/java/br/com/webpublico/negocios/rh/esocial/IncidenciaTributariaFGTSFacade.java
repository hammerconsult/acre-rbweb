package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.IncidenciaTributariaFGTS;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by William on 25/05/2018.
 */
@Stateless
public class IncidenciaTributariaFGTSFacade extends AbstractFacade<IncidenciaTributariaFGTS> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public IncidenciaTributariaFGTSFacade() {
        super(IncidenciaTributariaFGTS.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCodigoCadastrado(IncidenciaTributariaFGTS selecionado) {
        String sql = "select * from IncidenciaTributariaFGTS where codigo = :codigo";
        if (selecionado.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }
}
