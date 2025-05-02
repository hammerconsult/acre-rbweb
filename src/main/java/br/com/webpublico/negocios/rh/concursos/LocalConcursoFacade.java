package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.LocalConcurso;
import br.com.webpublico.enums.TipoLocalConcurso;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by venom on 29/10/14.
 */
@Stateless
public class LocalConcursoFacade extends AbstractFacade<LocalConcurso> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;

    public LocalConcursoFacade() {
        super(LocalConcurso.class);
    }

    @Override
    public LocalConcurso recuperar(Object id) {
        return super.recuperar(id);
    }

    @Override
    public void salvarNovo(LocalConcurso entity) {
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(LocalConcurso entity) {
        super.salvar(entity);
    }

    public List<LocalConcurso> listaPorTipo(TipoLocalConcurso tipo) {
        String hql = "select lc from LocalConcurso lc where lc.tipoLocalConcurso = :type";
        Query q = entityManager.createQuery(hql);
        q.setParameter("type", tipo);
        List<LocalConcurso> lista = q.getResultList();
        return lista;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
