package br.com.webpublico.negocios.tributario.procuradoria;

import br.com.webpublico.entidades.tributario.procuradoria.SituacaoTramiteJudicial;
import br.com.webpublico.entidades.tributario.procuradoria.Vara;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SituacaoTramiteJudicialFacade extends AbstractFacade<SituacaoTramiteJudicial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SituacaoTramiteJudicialFacade() {
        super(SituacaoTramiteJudicial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SituacaoTramiteJudicial recuperar(Object id) {
        return em.find(SituacaoTramiteJudicial.class, id);
    }

    public List<SituacaoTramiteJudicial> getSituacaoTramiteJudicial() {
        String sql = "select * from SituacaoTramiteJudicial s order by s.descricao asc";
        Query q = em.createNativeQuery(sql, SituacaoTramiteJudicial.class);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }
}
