/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.perguntasrespostas.AssuntoNfse;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AssuntoNfseFacade extends AbstractFacade<AssuntoNfse> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public AssuntoNfseFacade() {
        super(AssuntoNfse.class);
    }


    public Integer getProximaOrdem() {
        String sql = "select max(ordem) from assuntonfse";

        Query q = em.createNativeQuery(sql);

        try {
            return Integer.parseInt("" + q.getSingleResult()) + 1;
        } catch (Exception nre) {
            return 1;
        }
    }

    public List<AssuntoNfse> recuperarAssuntosOrdenadoPorOrdem() {
        String hql = "select a from AssuntoNfse a order by a.ordem";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public Page<AssuntoNfse> recuperarAssuntosPorBuscaAssunto(Pageable pageable, String query) {

        String hql = "select a from AssuntoNfse a where lower(a.descricao) like :busca";
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);
        Query q = em.createQuery(hql);
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);

        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        return new PageImpl(q.getResultList(), pageable, resultCount);
    }

    public Page<AssuntoNfse> recuperarAssuntos(Pageable pageable) {
        String hql = "select a from AssuntoNfse a";
        hql += PesquisaGenericaNfseUtil.montarOrdem(pageable);

        Query q = em.createQuery(hql);

        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);
        return new PageImpl(q.getResultList(), pageable, resultCount);
    }

    public Page<AssuntoNfse> recuperarAssuntosPorBuscaPerguntas(Pageable pageable, String query) {
        String sql = "SELECT DISTINCT A.* FROM ASSUNTONFSE A JOIN PERGUNTASRESPOSTAS P ON A.ID = P.ASSUNTO_ID";
        sql += PesquisaGenericaNfseUtil.montarWhere("pergunta,resposta", query);
        Query q = em.createNativeQuery(sql, AssuntoNfse.class);

        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);

        int resultCount = q.getResultList().size();

        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);

        return new PageImpl(q.getResultList(), pageable, resultCount);
    }


}
