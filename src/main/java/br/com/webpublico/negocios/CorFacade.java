/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cor;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Stateless
public class CorFacade extends AbstractFacade<Cor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CorFacade() {
        super(Cor.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Cor findByDescricao(String descricao) {
        String sql = "select * " +
            "       from cor " +
            "      where lower(descricao) = :descricao";

        Query q = em.createNativeQuery(sql, Cor.class);
        q.setParameter("descricao", descricao.toLowerCase());

        try {
            return (Cor) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }

    }

    public Cor findByParameters(Map<String, String> parameters) {
        String sql = "select * " +
            "       from cor" +
            "      where ";

        for (String key : parameters.keySet()) {
            sql += key + " = " + parameters.get(key) + " and ";
        }

        sql = sql.substring(0, sql.length() - 4);

        Query q = em.createNativeQuery(sql, Cor.class);
        return (Cor) q.getSingleResult();
    }

    public List<Cor> findListByDescricao(String descricao) {
        String sql = "select * " +
            "       from cor " +
            "      where lower(descricao) like :descricao";

        Query q = em.createNativeQuery(sql, Cor.class);
        q.setParameter("descricao", "%" + descricao.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public List<Cor> findListForAutoComplete(String parameter) {
        String sql = "select c.*" +
            "       from cor c" +
            "      where lower(c.descricao) like :parameter" +
            "         or lower(c.tiposituacaolicitacao) like :parameter";

        Query q = em.createNativeQuery(sql, Cor.class);
        q.setParameter("parameter", "%" + parameter.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public List<Cor> listarOrdenandoPorDescricaoDescrescente() {
        String sql = " select c.* " +
            "       from cor c " +
            " order by c.descricao desc ";

        Query q = em.createNativeQuery(sql, Cor.class);
        return q.getResultList();
    }

    public Cor salvarMerge(Cor cor) {
        return em.merge(cor);
    }
}
