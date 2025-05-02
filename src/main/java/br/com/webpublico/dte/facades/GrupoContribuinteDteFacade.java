package br.com.webpublico.dte.facades;

import br.com.webpublico.dte.entidades.GrupoContribuinteDte;
import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GrupoContribuinteDteFacade extends AbstractFacade<GrupoContribuinteDte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GrupoContribuinteDteFacade() {
        super(GrupoContribuinteDte.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public GrupoContribuinteDte recuperar(Object id) {
        GrupoContribuinteDte grupo = super.recuperar(id);
        Hibernate.initialize(grupo.getCadastros());
        return grupo;
    }

    public List<GrupoContribuinteDte> buscarGrupoPorDescricao(String descricao) {
        String sql = " select * from grupocontribuintedte " +
            " where lower(trim(descricao)) like :parte ";
        Query q = em.createNativeQuery(sql, GrupoContribuinteDte.class);
        q.setParameter("parte", "%" + descricao.trim().toLowerCase() + "%");
        return q.getResultList();
    }
}
