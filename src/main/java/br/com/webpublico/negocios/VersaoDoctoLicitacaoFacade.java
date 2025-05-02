/*
 * Codigo gerado automaticamente em Mon Dec 19 14:43:09 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoLicitacao;
import br.com.webpublico.entidades.VersaoDoctoLicitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class VersaoDoctoLicitacaoFacade extends AbstractFacade<VersaoDoctoLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VersaoDoctoLicitacaoFacade() {
        super(VersaoDoctoLicitacao.class);
    }

    public List<VersaoDoctoLicitacao> recuperaListaDeVersoesPorDocumento(DoctoLicitacao docto) {

        String hql = " from VersaoDoctoLicitacao versao where versao.doctoLicitacao = :docto";
        Query q = em.createQuery(hql);
        q.setParameter("docto", docto);
        try {
            return (List<VersaoDoctoLicitacao>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<VersaoDoctoLicitacao>();
        }
    }
}
