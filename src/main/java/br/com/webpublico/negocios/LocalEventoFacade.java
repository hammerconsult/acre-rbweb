package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LocalEvento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 25/05/15.
 */
@Stateless
public class LocalEventoFacade extends AbstractFacade<LocalEvento> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;

    public LocalEventoFacade() {
        super(LocalEvento.class);
    }

    @Override
    public void salvarNovo(LocalEvento entity) {
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(LocalEvento entity) {
        super.salvar(entity);
    }

    public List<LocalEvento> listaPorNome(LocalEvento nome) {
        String hql = "from LocalEvento where nomeLocalEvento = :nome";
        Query q = entityManager.createQuery(hql);
        q.setParameter("nome", nome);
        List<LocalEvento> lista = q.getResultList();
        return lista;
    }

    public boolean existeLocalEventoPorNome(String nome) {
        String hql = "from LocalEvento where nome = :nome";
        Query q = entityManager.createQuery(hql);
        q.setParameter("nome", nome);
        return !q.getResultList().isEmpty();
    }

    public boolean existeLocalEventoPorCodigo(String codigo) {
        String hql = "from LocalEvento where codigo = :codigo";
        Query q = entityManager.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public List<LocalEvento> completaLocalEvento(String filtro) {
        String hql = "from LocalEvento where lower(trim(nome)) like :filtro" +
                " or lower(trim(descricao)) like :filtro";
        Query q = entityManager.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }


}
