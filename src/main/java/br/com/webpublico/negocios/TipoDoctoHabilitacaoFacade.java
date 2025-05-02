/*
 * Codigo gerado automaticamente em Wed Dec 28 16:25:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoHabilitacao;
import br.com.webpublico.entidades.TipoDoctoHabilitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoDoctoHabilitacaoFacade extends AbstractFacade<TipoDoctoHabilitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoDoctoHabilitacaoFacade() {
        super(TipoDoctoHabilitacao.class);
    }

    public List<DoctoHabilitacao> recuperaDoctosPeloTipo(TipoDoctoHabilitacao tipoDoctoHabilitacao) {
        String hql = "select doc from DoctoHabilitacao doc"
                + " inner join doc.tipoDoctoHabilitacao tipo"
                + " where tipo= :tipo";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipoDoctoHabilitacao);
        try {
            return (List<DoctoHabilitacao>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<DoctoHabilitacao>();
        }


    }

    public boolean validaExclusao(TipoDoctoHabilitacao tipoDoctoHabilitacao) {
        String hql = "from DoctoHabilitacao doc"
                + " inner join doc.tipoDoctoHabilitacao tipo"
                + " where tipo = :tipo";
        Query query = em.createQuery(hql);
        query.setParameter("tipo", tipoDoctoHabilitacao);
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null ? false : true;
        } catch (NoResultException ex) {
            return true;
        }
    }

    public boolean validaDescricaoUnica(TipoDoctoHabilitacao tipo) {
        String hql = "select doc.descricao from TipoDoctoHabilitacao doc where lower(doc.descricao) = :descricao";
        if (tipo.getId() != null) {
            hql += " and doc.id <> :id";
        }
        Query query = em.createQuery(hql);
        query.setParameter("descricao", tipo.getDescricao().toLowerCase());
        if (tipo.getId() != null) {
            query.setParameter("id", tipo.getId());
        }
        query.setMaxResults(1);
        try {
            if (tipo.getDescricao().toLowerCase().equals(((String) query.getSingleResult()).toLowerCase())) {
                return false;
            } else {
                return true;
            }
        } catch (NoResultException e) {
            return true;
        }
    }

    public List<TipoDoctoHabilitacao> listaFiltrando(String s) {
        String hql = "from TipoDoctoHabilitacao obj where lower(obj.descricao) like :filtro";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }
}
