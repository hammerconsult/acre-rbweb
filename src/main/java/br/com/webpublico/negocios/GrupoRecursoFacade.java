/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.RecursoSistema;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class GrupoRecursoFacade extends AbstractFacade<GrupoRecurso> {

    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoRecursoFacade() {
        super(GrupoRecurso.class);
    }

    @Override
    public GrupoRecurso recuperar(Object id) {
        GrupoRecurso grupoRecurso = em.find(GrupoRecurso.class, id);
        grupoRecurso.getRecursos().size();
        return grupoRecurso;
    }

    public RecursoSistemaFacade getRecursoSistemaFacade() {
        return recursoSistemaFacade;
    }

    public void salvar2(GrupoRecurso entity) {
        List<RecursoSistema> recursos = new ArrayList<RecursoSistema>();
        for (RecursoSistema rs : entity.getRecursos()) {
            recursos.add(em.find(RecursoSistema.class, rs.getId()));

        }
        entity.getRecursos().clear();
        entity.getRecursos().addAll(recursos);
        em.merge(entity);
    }

    public void salvarNovo2(GrupoRecurso entity) {
        List<RecursoSistema> recursos = new ArrayList<RecursoSistema>();
        for (RecursoSistema rs : entity.getRecursos()) {
            recursos.add(em.find(RecursoSistema.class, rs.getId()));

        }
        entity.getRecursos().clear();
        entity.getRecursos().addAll(recursos);
        em.persist(entity);
    }

    public boolean existeGrupoUsuario(GrupoRecurso grupoRecurso) {
        String hql = "from ItemGrupoUsuario where grupoRecurso = :grupoRecurso";
        Query q = em.createQuery(hql);
        q.setParameter("grupoRecurso", grupoRecurso);
        return !q.getResultList().isEmpty();
    }

    public List<GrupoRecurso> listaOrdenado() {
        String hql = "from GrupoRecurso order by nome";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public boolean jaExisteNomeGrupoRecurso(GrupoRecurso grupoRecurso) {
        String hql = "from GrupoRecurso where lower(nome) = :nome and id <> :id";
        Query q = em.createQuery(hql);
        q.setParameter("nome", grupoRecurso.getNome().toLowerCase());
        Long id = 0L;
        if (grupoRecurso.getId() != null) {
            id = grupoRecurso.getId();
        }
        q.setParameter("id", id);
        return !q.getResultList().isEmpty();
    }

    public List<GrupoRecurso> buscarGrupoRecursoPorNome(String filter) {
        String sql = " select gr.* from GrupoRecurso gr where lower(gr.nome) like :filter ";

        Query q = em.createNativeQuery(sql, GrupoRecurso.class);
        q.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty())
            return q.getResultList();
        return new ArrayList<>();
    }

}
