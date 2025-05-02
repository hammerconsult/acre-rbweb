package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.rh.saudeservidor.SituacaoAcidente;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author alex on 22/09/2016  10:43.
 */
@Stateless
public class SituacaoAcidenteFacade extends AbstractFacade<SituacaoAcidente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SituacaoAcidenteFacade() {
        super(SituacaoAcidente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<SituacaoAcidente> buscarFiltrandoPorCodigoAndDescricao(String parte) {
        String sql = " select * " +
            "          from SITUACAOACIDENTE " +
            "          where (codigo like :parte or descricao like :parte) ";
        Query q = em.createNativeQuery(sql, SituacaoAcidente.class);
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }
}
