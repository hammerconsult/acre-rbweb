/*
 * Codigo gerado automaticamente em Wed Dec 14 17:32:26 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoLicitacao;
import br.com.webpublico.entidades.ModeloDocto;
import br.com.webpublico.entidades.TipoModeloDocto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ModeloDoctoFacade extends AbstractFacade<ModeloDocto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModeloDoctoFacade() {
        super(ModeloDocto.class);
    }

    public Integer recuperaNumeroSequencialDoctoLicitacao() {
        String hql = "select max(l.numero) from DoctoLicitacao l";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (Integer) q.getSingleResult() + 1;
        } else {
            return 1;
        }

    }

    public DoctoLicitacao modeloDoctoPodeSerExcluir(ModeloDocto modeloDocto) {
        String hql = "select docto from DoctoLicitacao docto"
            + " inner join docto.modeloDocto modelo"
            + " where modelo =:modelo";
        Query q = em.createQuery(hql);
        q.setParameter("modelo", modeloDocto);
        q.setMaxResults(1);
        try {
            if (q.getSingleResult() != null) {
                return (DoctoLicitacao) q.getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }
        return null;
    }

    public List<ModeloDocto> completaModeloDocto(String trim) {
        String hql = " select distinct md from ModeloDocto md"
            + " where md.nome like :parte or md in (select modeloDocto from DoctoLicitacao)";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + trim + "%");
        try {
            return (List<ModeloDocto>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<ModeloDocto>();
        }

    }

    public List<ModeloDocto> recuperaModelosRaiz() {
        String hql = "select modelo from ModeloDocto modelo"
            + " where modelo not in (select modeloDocto from VersaoDoctoLicitacao)";
        Query q = em.createQuery(hql);
        try {
            return (List<ModeloDocto>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<ModeloDocto>();
        }
    }

    public ModeloDocto recuperarModeloDocumentoPorTipo(TipoModeloDocto.TipoModeloDocumento tipo) {
        String sql = "select * from modelodocto where TIPOMODELODOCTO = :tipoModelo";
        Query q = em.createNativeQuery(sql, ModeloDocto.class);
        q.setParameter("tipoModelo", tipo.name());
        if (!q.getResultList().isEmpty()) {
            return (ModeloDocto) q.getResultList().get(0);
        }
        return null;
    }
}
