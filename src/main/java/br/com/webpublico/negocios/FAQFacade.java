package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FAQ;
import br.com.webpublico.enums.ModuloFAQ;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 12/07/14
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FAQFacade extends AbstractFacade<FAQ> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FAQFacade() {
        super(FAQ.class);
    }

    public List<FAQ> getFAQsPorModulo(ModuloFAQ moduloFAQ, String filtro) {
        String sql = "select faq.* from FAQ faq " +
                " where faq.modulo = '" + moduloFAQ.name() + "' " +
                " and (lower(faq.pergunta) like :filtro " +
                "  or lower(faq.resposta) like :filtro" +
                "  or lower(faq.assunto) like :filtro)" +
                " order by faq.assunto";
        Query consulta = em.createNativeQuery(sql, FAQ.class);
        consulta.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return consulta.getResultList();
    }
}
