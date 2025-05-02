/*
 * Codigo gerado automaticamente em Thu Dec 29 10:16:56 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoHabilitacao;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class DoctoHabilitacaoFacade extends AbstractFacade<DoctoHabilitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DoctoHabilitacaoFacade() {
        super(DoctoHabilitacao.class);
    }

    public boolean validarExclusao(DoctoHabilitacao doctoHabilitacao) {
        String hql = "select doc from DoctoHabLicitacao hab"
            + " inner join hab.licitacao lic"
            + " inner join hab.doctoHabilitacao doc"
            + " where doc= :doc";
        Query query = em.createQuery(hql);
        query.setParameter("doc", doctoHabilitacao);
        query.setMaxResults(1);
        try {
            if (query.getSingleResult() != null) {
                return false;
            }
        } catch (NoResultException e) {
            return true;
        }
        return true;
    }

    public boolean validarDescricaoUnica(DoctoHabilitacao doc) {
        String hql = "select doc.descricao from DoctoHabilitacao doc where lower(doc.descricao) = :descricao";
        if (doc.getId() != null) {
            hql += " and doc.id <> :id";
        }
        Query query = em.createQuery(hql);
        query.setParameter("descricao", doc.getDescricao().toLowerCase());
        if (doc.getId() != null) {
            query.setParameter("id", doc.getId());
        }
        query.setMaxResults(1);
        try {
            if (doc.getDescricao().toLowerCase().equals(((String) query.getSingleResult()).toLowerCase())) {
                return false;
            } else {
                return true;
            }
        } catch (NoResultException e) {
            return true;
        }

    }

    public List<DoctoHabilitacao> buscarDocumentosVigentes(String parte, Date dataReferencia) {
        String sql = " select doc.* " +
            " from DoctoHabilitacao doc " +
            " where to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(doc.inicioVigencia) and coalesce(trunc(doc.fimVigencia), to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "   and lower(doc.descricao) like :parte ";
        Query q = em.createNativeQuery(sql, DoctoHabilitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        return q.getResultList();
    }

}
