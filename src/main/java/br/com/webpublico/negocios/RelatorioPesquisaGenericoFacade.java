package br.com.webpublico.negocios;

import br.com.webpublico.enums.TipoRelatorio;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 17/07/13
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelatorioPesquisaGenericoFacade extends AbstractFacade<RelatorioPesquisaGenerico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RelatorioPesquisaGenericoFacade() {
        super(RelatorioPesquisaGenerico.class);
    }

    @Override
    protected EntityManager getEntityManager() {

        return em;
    }

    public List<Class> getAllClass() {
        List<Class> retorno = new ArrayList<Class>();
        for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
            retorno.add(entityType.getJavaType());
        }

        return retorno;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        if (entidade.equals(RelatorioPesquisaGenerico.class)) {
            RelatorioPesquisaGenerico relatorioPesquisaGenerico = em.find(RelatorioPesquisaGenerico.class, id);
            relatorioPesquisaGenerico.getTarget().size();
            relatorioPesquisaGenerico.getSource().size();
            return relatorioPesquisaGenerico;
        }
        return super.recuperar(entidade, id);
    }

    @Override
    public RelatorioPesquisaGenerico recuperar(Object id) {
        RelatorioPesquisaGenerico relatorioPesquisaGenerico = em.find(RelatorioPesquisaGenerico.class, id);
        relatorioPesquisaGenerico.getTarget().size();
        relatorioPesquisaGenerico.getSource().size();
        return relatorioPesquisaGenerico;
    }

    public RelatorioPesquisaGenerico recuperaRelatorioPorClasseTipoPadrao(String classe, TipoRelatorio tipoRelatorio) {
        String hql = "select r from RelatorioPesquisaGenerico r "
                + " where r.classe = :classe"
                + " and r.tipoRelatorio = :tipoRelatorio"
                + " and r.padrao = true"
                + " order by r.id desc";
        Query consulta = em.createQuery(hql, RelatorioPesquisaGenerico.class);
        consulta.setParameter("classe", classe);
        consulta.setParameter("tipoRelatorio", tipoRelatorio);
        consulta.setMaxResults(1);
        try {
            RelatorioPesquisaGenerico rpg = (RelatorioPesquisaGenerico) consulta.getSingleResult();
            rpg.getSource().size();
            rpg.getTarget().size();
            return rpg;
        } catch (NoResultException e) {
            return recuperaRelatorioPorClasseTipoNaoPadrao(classe, tipoRelatorio);
        }
    }

    public RelatorioPesquisaGenerico recuperaRelatorioPorClasseTipoNaoPadrao(String classe, TipoRelatorio tipoRelatorio) {
        String hql = "select r from RelatorioPesquisaGenerico r "
                + " where r.classe = :classe"
                + " and r.tipoRelatorio = :tipoRelatorio"
                + " and r.padrao = false"
                + " order by r.id desc";
        Query consulta = em.createQuery(hql, RelatorioPesquisaGenerico.class);
        consulta.setParameter("classe", classe);
        consulta.setParameter("tipoRelatorio", tipoRelatorio);
        consulta.setMaxResults(1);
        try {
            RelatorioPesquisaGenerico rpg = (RelatorioPesquisaGenerico) consulta.getSingleResult();
            rpg.getSource().size();
            rpg.getTarget().size();
            return rpg;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long retornaUltimoCodigoPorClasse(String classe) {
        Query consulta = em.createNativeQuery("SELECT coalesce(max(o.codigo) + 1, 1) FROM RELATORIOPESQUISAGENERICO o WHERE o.classe = :classe");
        consulta.setParameter("classe", classe);
        try {
            return ((BigDecimal) consulta.getSingleResult()).longValue();
        } catch (Exception e) {
            return 1l;
        }
    }

    public List<RelatorioPesquisaGenerico> listaFiltrandoPorClasse(String s, String classe, TipoRelatorio tipoRelatorio) {
        String hql = "from " + super.getClasse().getSimpleName() + " obj " +
                " where obj.classe = :classe " +
                "  and obj.tipoRelatorio = :tipoRelatorio" +
                "  and (to_char(obj.codigo) like :desc or " +
                "      (lower(obj.nomeRelatorio) like :desc or " +
                "       lower(obj.titulo) like :desc))";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("classe", classe);
        q.setParameter("tipoRelatorio", tipoRelatorio);
        q.setParameter("desc", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        try {
            List<RelatorioPesquisaGenerico> lista = q.getResultList();
            return lista;
        } catch (Exception e) {
            return new ArrayList<RelatorioPesquisaGenerico>();
        }
    }

    public RelatorioPesquisaGenerico validaRelatorioPadrao(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        String hql = "select r from RelatorioPesquisaGenerico  r where r.padrao = true and r.tipoRelatorio = :tipoRelatorio and r.classe = :classe";
        if (relatorioPesquisaGenerico.getId() != null) {
            hql += " and r.id <> :id";
        }
        Query consulta = em.createQuery(hql, RelatorioPesquisaGenerico.class);
        consulta.setParameter("tipoRelatorio", relatorioPesquisaGenerico.getTipoRelatorio());
        consulta.setParameter("classe", relatorioPesquisaGenerico.getClasse());
        if (relatorioPesquisaGenerico.getId() != null) {
            consulta.setParameter("id", relatorioPesquisaGenerico.getId());
        }
        consulta.setMaxResults(1);
        try {
            return (RelatorioPesquisaGenerico) consulta.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
