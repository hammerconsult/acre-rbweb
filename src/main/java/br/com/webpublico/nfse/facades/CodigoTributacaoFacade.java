package br.com.webpublico.nfse.facades;

import br.com.webpublico.nfse.domain.CodigoTributacao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CodigoTributacaoFacade extends AbstractFacade<CodigoTributacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CodigoTributacaoFacade() {
        super(CodigoTributacao.class);
    }

    public List<CodigoTributacao> completarCodigoTributacao(String parte) {
        String hql = "SELECT ct.* " +
            "   FROM CodigoTributacao ct " +
            "  WHERE (lower(ct.descricao) LIKE :parte OR lower(ct.codigo) = :numero) ";
        Query q = em.createNativeQuery(hql, CodigoTributacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("numero", parte.toLowerCase());
        q.setMaxResults(10);
        return q.getResultList();
    }


}
