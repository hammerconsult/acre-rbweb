package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.IncidenciaTributariaPrevidencia;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by William on 24/05/2018.
 */
@Stateless
public class IncidenciaTributariaPrevidenciaFacade extends AbstractFacade<IncidenciaTributariaPrevidencia> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public IncidenciaTributariaPrevidenciaFacade() {
        super(IncidenciaTributariaPrevidencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCodigoCadastrado(IncidenciaTributariaPrevidencia selecionado) {
        String sql = "select * from INCIDENCIATRIBPREVIDENCIA where codigo = :codigo";
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
