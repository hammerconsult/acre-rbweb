package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ModeloDocumentoDteFacade extends AbstractFacade<ModeloDocumentoDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ModeloDocumentoDteFacade() {
        super(ModeloDocumentoDte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ModeloDocumentoDte> buscarModeloPorDescricao(String descricao) {
        String sql = " select * from modelodocumentodte " +
            " where lower(trim(descricao)) like :parte ";
        Query q = em.createNativeQuery(sql, ModeloDocumentoDte.class);
        q.setParameter("parte", "%" + descricao.trim().toLowerCase() + "%");
        return q.getResultList();
    }
}
