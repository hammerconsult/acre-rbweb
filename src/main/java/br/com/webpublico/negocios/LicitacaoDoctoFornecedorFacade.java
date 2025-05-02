/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LicitacaoDoctoFornecedor;
import br.com.webpublico.entidades.LicitacaoFornecedor;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless

public class LicitacaoDoctoFornecedorFacade extends AbstractFacade<LicitacaoDoctoFornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoDoctoFornecedorFacade() {
        super(LicitacaoDoctoFornecedor.class);
    }

    public List<LicitacaoDoctoFornecedor> recuperarDocumentosDaLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedorParametro) {
        String hql = "from LicitacaoDoctoFornecedor as ldf"
                + " where ldf.licitacaoFornecedor = :licitacaoFornecedorParametro";
        Query query = em.createQuery(hql);
        query.setParameter("licitacaoFornecedorParametro", licitacaoFornecedorParametro);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<LicitacaoDoctoFornecedor>();
        }
    }
}
