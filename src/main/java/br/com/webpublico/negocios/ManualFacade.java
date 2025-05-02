package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Manual;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ManualFacade extends AbstractFacade<Manual> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ManualFacade() {
        super(Manual.class);
    }

    @Override
    public Manual recuperar(Object id) {
        Manual manual = super.recuperar(id);
        if (manual.getArquivo() != null) {
            manual.getArquivo().getPartes().size();
        }
        return manual;
    }

    public List<Manual> lista(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            return lista();
        }
        Query q = em.createQuery("select m from Manual m where lower(m.descricao) like :filtro or lower(m.tags) like :filtro")
                .setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();  //To change body of created methods use File | Settings | File Templates.
    }
}
