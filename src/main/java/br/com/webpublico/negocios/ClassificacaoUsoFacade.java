package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClassificacaoUso;
import br.com.webpublico.exception.ValidacaoException;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ClassificacaoUsoFacade extends AbstractFacade<ClassificacaoUso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ClassificacaoUsoFacade() {
        super(ClassificacaoUso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ClassificacaoUso recuperar(Object id) {
        ClassificacaoUso classificacaoUso = super.recuperar(id);
        Hibernate.initialize(classificacaoUso.getItemList());
        return classificacaoUso;
    }

    public boolean hasCodigoRegistrado(ClassificacaoUso classificacaoUso) {
        String hql = "from ClassificacaoUso where codigo = : codigo";
        if (classificacaoUso.getId() != null) {
            hql += " and id != :id ";
        }
        Query query = em.createQuery(hql);
        query.setParameter("codigo", classificacaoUso.getCodigo());
        if (classificacaoUso.getId() != null) {
            query.setParameter("id", classificacaoUso.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @Override
    public void preSave(ClassificacaoUso entidade) {
        if (hasCodigoRegistrado(entidade)) {
            throw new ValidacaoException("O código informado já está registrado.");
        }
    }
}
