/*
 * Codigo gerado automaticamente em Thu Dec 15 15:51:39 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoLicitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class DoctoLicitacaoFacade extends AbstractFacade<DoctoLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DoctoLicitacaoFacade() {
        super(DoctoLicitacao.class);
    }

//    public boolean validaNumero(DoctoLicitacao dl) {
//
//        String hql = " from DoctoLicitacao d where d.numero = :num";
//
//        Query q = getEntityManager().createQuery(hql);
//        q.setParameter("num", dl.getNumero());
//
//        List<DoctoLicitacao> lista = q.getResultList();
//
//        if (lista.contains(dl)) {
//            return false;
//        } else {
//            return (!q.getResultList().isEmpty());
//        }
//    }
}
