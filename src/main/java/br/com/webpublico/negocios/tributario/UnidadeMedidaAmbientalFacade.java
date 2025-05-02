package br.com.webpublico.negocios.tributario;


import br.com.webpublico.entidades.tributario.UnidadeMedidaAmbiental;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UnidadeMedidaAmbientalFacade extends AbstractFacade<UnidadeMedidaAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public UnidadeMedidaAmbientalFacade() {
        super(UnidadeMedidaAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<UnidadeMedidaAmbiental> unidadesMedidaAmbientalAtivas() {
        String sql = "select un.* from unidademedidaambiental un where ativo = 1";
        Query q = em.createNativeQuery(sql, UnidadeMedidaAmbiental.class);
        return q.getResultList();
    }

    public List<UnidadeMedidaAmbiental> unidadesMedidaAmbientalAtivasPorAssunto(Long idAssunto) {
        String sql = "select un.* from unidademedidaambiental un " +
            " where ativo = 1 and (un.assuntolicenciamentoambiental_id = :idAssunto or un.assuntolicenciamentoambiental_id is null)";
        Query q = em.createNativeQuery(sql, UnidadeMedidaAmbiental.class);
        q.setParameter("idAssunto", idAssunto);
        return q.getResultList();
    }
}
