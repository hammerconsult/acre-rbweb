package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MetodoCientifico;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 26/05/15.
 */
@Stateless
public class MetodoCientificoFacade extends AbstractFacade<MetodoCientifico>{
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;

    public MetodoCientificoFacade() {
        super(MetodoCientifico.class);
    }

    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;

    @Override
    public void salvarNovo(MetodoCientifico entity) {
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(MetodoCientifico entity) {
        super.salvar(entity);
    }

    public List<MetodoCientifico> listaPorNome(MetodoCientifico nome) {
        String hql = "select mc from MetodoCientifico mc where mc.nomeMetodoCientifico = :nome";
        Query q = entityManager.createQuery(hql);
        q.setParameter("nome", nome);
        List<MetodoCientifico> lista = q.getResultList();
        return lista;
    }

    public boolean existeMetodoCientificoPorNome(String nome) {
        String hql = "select mc from MetodoCientifico mc where mc.nome = :nome";
        Query q = entityManager.createQuery(hql);
        q.setParameter("nome", nome);
        return !q.getResultList().isEmpty();
    }

    public boolean existeMetodoCientificoPorCodigo(String codigo) {
        String hql = "select mc from MetodoCientifico mc where mc.codigo = :codigo";
        Query q = entityManager.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public List<MetodoCientifico> completaMetodoCientifico(String filtro) {
        String hql = "select mc from MetodoCientifico mc where lower(trim(mc.nome)) like :filtro" +
                " or lower(trim(mc.descricao)) like :filtro";
        Query q = entityManager.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }
}
