/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.perguntasrespostas.PerguntasRespostas;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PerguntasRespostasFacade extends AbstractFacade<PerguntasRespostas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PerguntasRespostasFacade() {
        super(PerguntasRespostas.class);
    }

    public List<PerguntasRespostas> buscarPergunstasRespostasParaExibicao() {
        String hql = " select pr from PerguntasRespostas pr " +
            " join pr.assunto a " +
            " where a.habilitarExibicao = :verdadeiro " +
            "   and pr.habilitarExibicao = :verdadeiro " +
            " order by a.ordem, pr.ordem ";
        Query q = em.createQuery(hql);
        q.setParameter("verdadeiro", true);
        return q.getResultList();
    }
}
