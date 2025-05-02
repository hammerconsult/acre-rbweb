package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoCadastralImovel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/06/14
 * Time: 09:11
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AlteracaoCadastralImovelFacade extends AbstractFacade<AlteracaoCadastralImovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlteracaoCadastralImovelFacade() {
        super(AlteracaoCadastralImovel.class);
    }

    public Long retornaUltimoNumeroDoAno(Integer ano) {
        Query q = em.createNativeQuery(" select coalesce(cast(max(substr(registro, 5, 4)) as numeric(4)), 0) numero from alteracaocadastralimovel where substr(registro, 1, 4) = :ano ");
        q.setParameter("ano", ano);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            return ((BigDecimal) q.getSingleResult()).longValue() + 1l;
        }
        return 1l;
    }
}
