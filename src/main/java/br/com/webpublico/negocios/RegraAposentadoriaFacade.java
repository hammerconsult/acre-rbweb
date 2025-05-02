package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RegraAposentadoria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class RegraAposentadoriaFacade extends AbstractFacade<RegraAposentadoria> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RegraAposentadoriaFacade() {
        super(RegraAposentadoria.class);
    }

    @Override
    public List<RegraAposentadoria> lista() {
        return super.lista();
    }

    public List<RegraAposentadoria> buscarRegraAposentadoriaPorTipoAposentadoria(Long id) {
        String sql = "SELECT regra.* from RegraAposentadoria regra " +
            " where regra.tipoaposentadoria_id = :tipoId";
        Query q = em.createNativeQuery(sql, RegraAposentadoria.class);
        q.setParameter("tipoId", id);
        return q.getResultList();
    }

}
